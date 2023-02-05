package com.tetris.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tetris.game.TetrisGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Tetris");
		config.setWindowPosition(600,0);
		config.setWindowedMode(780, 920);
		config.setResizable(false);
		new Lwjgl3Application(new TetrisGame(), config);
	}
}
