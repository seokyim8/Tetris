package com.tetris.game;

import com.badlogic.gdx.ScreenAdapter;

import io.socket.client.Socket;

public class LobbyScreen extends ScreenAdapter {
    TetrisGame tetris_game;
    Socket socket;

    public LobbyScreen(TetrisGame tetris_game){
        this.tetris_game = tetris_game;
        socket = tetris_game.roomListingScreen.socket;
    }

    @Override
    public void show(){
        //TODO
    }

    @Override
    public void render(float dt){
        //TODO
    }

    @Override
    public void dispose(){

    }

    @Override
    public void hide(){
        dispose();
    }
}

