package com.tetris.game;

import com.badlogic.gdx.ScreenAdapter;

public class MultiPlayerGameScreen extends ScreenAdapter {
    TetrisGame tetris_game;

    public MultiPlayerGameScreen(TetrisGame tetris_game){
        this.tetris_game = tetris_game;
    }
}
