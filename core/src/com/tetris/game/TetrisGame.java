package com.tetris.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tetris.game.sprites.Container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TetrisGame extends Game {
	//TODO: WE NEED TO KEEP THE INSTANCES OF SCREENS BECAUSE WHEN SETTING TO A NEW SCREEN THE PREVIOUS SCREEN INSTANCE DOES NOT GO AWAY BY ITSELF
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	TitleScreen titleScreen;
	GameScreen gameScreen;
	RoomListingScreen roomListingScreen;
	MultiPlayerGameScreen multiPlayerGameScreen;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		titleScreen = new TitleScreen(this);
		gameScreen = new GameScreen(this);
		roomListingScreen = new RoomListingScreen(this);
		multiPlayerGameScreen = new MultiPlayerGameScreen(this);

		setScreen(titleScreen);
	}

	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
	}
}
