package com.tetris.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import java.util.Hashtable;

public class TitleScreen extends ScreenAdapter {
    TetrisGame tetris_game;
    final BitmapFont title = new BitmapFont();
    final String title_string = "TETRIS (Extra simple gameplay)";
    GlyphLayout gl;

    Sprite to_room_listing_screen;
    Texture to_room_listing_screen_texture;
    Sprite to_game_screen;
    Texture to_game_screen_texture;
    final static int button_width = 176;
    final static int button_height = 88;
    Hashtable<String, int[]> button_to_location;

    public TitleScreen(TetrisGame tetris_game){
        this.tetris_game = tetris_game;
    }

    @Override
    public void show(){
        gl = new GlyphLayout(title, title_string);
        //TODO: ADD PNG FILES FOR THE BUTTONS
        //initializing textures
        to_room_listing_screen_texture = new Texture("room_listing.png");
        to_room_listing_screen = new Sprite(to_room_listing_screen_texture);
        to_game_screen_texture = new Texture("game_screen.png");
        to_game_screen = new Sprite(to_game_screen_texture);

        button_to_location = new Hashtable<>();
        button_to_location.put("to_game_screen", new int[]{Gdx.graphics.getWidth() /2 - button_width/2, Gdx.graphics.getHeight()/2});
        button_to_location.put("to_room_listing", new int[]{Gdx.graphics.getWidth() /2 -button_width/2, Gdx.graphics.getHeight()/4});
        to_room_listing_screen.setSize(button_width,button_height);
        to_game_screen.setSize(button_width, button_height);
        to_room_listing_screen.setPosition(button_to_location.get("to_room_listing")[0], button_to_location.get("to_room_listing")[1]);
        to_game_screen.setPosition(button_to_location.get("to_game_screen")[0], button_to_location.get("to_game_screen")[1]);


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
                int y = Gdx.input.getY();// SCREEN ORIGIN IS TOP LEFT CORNER!! DIFFERENT FROM LIBGDX ORIGIN, WHICH IS BOTTOM LEFT

                if(x >= button_to_location.get("to_game_screen")[0] && x <= button_to_location.get("to_game_screen")[0] + button_width
                && y >= button_to_location.get("to_game_screen")[1] - button_height&& y <= button_to_location.get("to_game_screen")[1]){
                    //NEED TO CALL DISPOSE BEFORE SETTING TO A NEW SCREEN
                    dispose();
                    tetris_game.setScreen(new GameScreen(tetris_game));
                }
                return true;
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
        title.draw(tetris_game.batch, title_string, Gdx.graphics.getWidth() /2 - gl.width/2,
                Gdx.graphics.getHeight() - gl.height * 6);//centering title font
        to_room_listing_screen.draw(tetris_game.batch);
        to_game_screen.draw(tetris_game.batch);
        tetris_game.batch.end();
    }

    @Override
    public void dispose(){
        title.dispose();
        to_game_screen_texture.dispose();
        to_room_listing_screen_texture.dispose();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}
