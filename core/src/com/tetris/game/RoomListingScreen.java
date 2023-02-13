package com.tetris.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Hashtable;

public class RoomListingScreen extends ScreenAdapter {
    TetrisGame tetris_game;
    Texture room_listing_page_texture;
    Sprite room_listing_page;
    final static int x_padding = 20;
    final static int y_padding = 20;
    Hashtable<String, int[]> button_locations;
    Hashtable<String, int[]> button_dimensions;

    public RoomListingScreen(TetrisGame tetris_game){
        this.tetris_game = tetris_game;
    }

    @Override
    public void show(){
        room_listing_page_texture = new Texture("room_listing_page.png");
        room_listing_page = new Sprite(room_listing_page_texture);
        button_locations = new Hashtable<>();
        button_locations.put("to_title_screen", new int[]{40, 865});
        button_locations.put("left_arrow", new int[]{185, 785});
        button_locations.put("right_arrow", new int[]{501, 785});
        button_dimensions = new Hashtable<>();
        button_dimensions.put("to_title_screen", new int[]{162, 58});
        button_dimensions.put("left_arrow", new int[]{62, 32});
        button_dimensions.put("right_arrow", new int[]{62, 32});

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
                    dispose();
                    tetris_game.setScreen(new TitleScreen(tetris_game));
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

    @Override
    public void render(float dt){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tetris_game.batch.begin();
        room_listing_page.setPosition(x_padding,y_padding);
        room_listing_page.draw(tetris_game.batch);
        tetris_game.batch.end();
    }

    @Override
    public void dispose(){
        room_listing_page_texture.dispose();
    }

}
