package com.tetris.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class TitleScreen extends ScreenAdapter {
    TetrisGame tetris_game;
    BitmapFont title;

    public TitleScreen(TetrisGame tetris_game){
        this.tetris_game = tetris_game;
    }

    @Override
    public void show(){
        title = new BitmapFont();
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.SPACE){
                    tetris_game.setScreen(new GameScreen(tetris_game));
                }
                return true;
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
        tetris_game.batch.begin();
        title.draw(tetris_game.batch, "TETRIS (Extra simple gameplay)\nSpace bar to begin",
                Gdx.graphics.getWidth() /2 - 100, Gdx.graphics.getHeight() /2);
        tetris_game.batch.end();
    }

    @Override
    public void dispose(){
        title.dispose();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}
