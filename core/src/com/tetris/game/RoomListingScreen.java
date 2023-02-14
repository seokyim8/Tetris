package com.tetris.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomListingScreen extends ScreenAdapter {
    TetrisGame tetris_game;
    Texture room_listing_page_texture;
    Sprite room_listing_page;
    Texture create_room_texture;
    Sprite create_room;
    final static int x_padding = 20;
    final static int y_padding = 20;
    Hashtable<String, int[]> button_locations;
    Hashtable<String, int[]> button_dimensions;
    Socket socket;
    Hashtable<String,Room> room_list;

    class Room{
        String id;
        String title;
        String player_id;
        List<String> players;
        boolean is_playing;
        public Room(String id, String title, List<String> players){
            this.id = id;
            this.title = title;
            this.players = players;
            this.is_playing = false;
        }
        @Override
        public String toString(){
            return "Room id: " + this.id  + ", title: " + this.title;
        }
    }


    public RoomListingScreen(TetrisGame tetris_game){
        this.tetris_game = tetris_game;
    }

    @Override
    public void show(){
        //initializing textures and sprites
        room_listing_page_texture = new Texture("room_listing_page.png");
        room_listing_page = new Sprite(room_listing_page_texture);
        create_room_texture = new Texture("create_room.png");
        create_room = new Sprite(create_room_texture);

        //initializing Hashtables
        //the locations here are locations regarding the input location, meaning the origin is top left corner, not bottom right
        button_locations = new Hashtable<>();
        button_locations.put("to_title_screen", new int[]{40, 865});
        button_locations.put("left_arrow", new int[]{185, 785});
        button_locations.put("right_arrow", new int[]{501, 785});
        button_locations.put("create_room", new int[]{315, 865});
        button_dimensions = new Hashtable<>();
        button_dimensions.put("to_title_screen", new int[]{162, 58});
        button_dimensions.put("left_arrow", new int[]{62, 32});
        button_dimensions.put("right_arrow", new int[]{62, 32});
        button_dimensions.put("create_room", new int[]{160, 60});

        room_list = new Hashtable<>();

        config_socket();
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int x = Gdx.input.getX();
                int y = Gdx.input.getY();

                int[][] to_title_screen = new int[][]{button_locations.get("to_title_screen"), button_dimensions.get("to_title_screen")};
                if(x >= to_title_screen[0][0] && x <= to_title_screen[0][0] + to_title_screen[1][0] &&
                y >= to_title_screen[0][1] - to_title_screen[1][1] && y <= to_title_screen[0][1]){
                    tetris_game.setScreen(tetris_game.titleScreen);
                }

                int[][] create_room = new int[][]{button_locations.get("create_room"), button_dimensions.get("create_room")};
                if(x >= create_room[0][0] && x <= create_room[0][0] + create_room[1][0] &&
                y >= create_room[0][1] - create_room[1][1] && y <= create_room[0][1]){
                    //TODO: CREATE ROOM FUNCTIONALITY
                    socket.emit("create_room", "ROOM NAME NOT SPECIFIED");
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        });
    }

    private void config_socket(){
        //creating socket for connecting to server
        try {
            socket = IO.socket("http://localhost:8080");
            socket.connect();
        } catch (URISyntaxException e) {
            System.out.println("Socket creation failed for connecting to the server, returning to title screen. " +
                    "Refer to the following error message.");
            System.out.println(e);
            tetris_game.setScreen(tetris_game.titleScreen);
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Successfully connected to server.");
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Server connection lost. Reattempting connection.");
            }
        }).on("created_room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject info = (JSONObject)args[0];
                try {
                    JSONObject temp = info.getJSONObject("room_list");
                    room_list = new Hashtable<>();
                    Iterator iterator = temp.keys();
                    while(iterator.hasNext()){
                        String key = (String)iterator.next();
                        List<String> players = new ArrayList<>();
                        JSONObject room_info = temp.getJSONObject(key);
                        for(int i = 0; i < room_info.getJSONArray("players").length(); i++){
                            players.add(room_info.getJSONArray("players").get(i).toString());
                        }
                        room_list.put(key, new Room(key,room_info.getString("title"), players));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                //debugging
                for(String key: room_list.keySet()){
                    System.out.println(room_list.get(key));
                }
                //
            }
        });
    }

    @Override
    public void render(float dt){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tetris_game.batch.begin();
        room_listing_page.setPosition(x_padding,y_padding);
        room_listing_page.draw(tetris_game.batch);
        create_room.setPosition(button_locations.get("create_room")[0], Gdx.graphics.getHeight() - button_locations.get("create_room")[1]);
        create_room.draw(tetris_game.batch);
        tetris_game.batch.end();
    }

    @Override
    public void dispose(){
        if(socket != null){
            socket.off();
            socket.close();
        }
        room_listing_page_texture.dispose();
        create_room_texture.dispose();
    }

    @Override
    public void hide(){
        dispose();
    }
}
