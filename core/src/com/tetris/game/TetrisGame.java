package com.tetris.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tetris.game.sprites.Container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TetrisGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture container_texture;
	Container container;
	Texture red_block;
	Texture blue_block;
	Texture orange_block;
	Texture green_block;
	Texture yellow_block;
	Texture light_blue_block;
	Texture purple_block;
	Texture game_over_sign;
	int time_passed;
	Grid[][] board;
	Piece current_piece;
	boolean game_over;
	final int x_padding = 20;
	final int y_padding = 15;
	List<Color> upcoming_blocks;
	List<Color> template_list;

	@Override
	public void create () {
		batch = new SpriteBatch();
		//setting up the container for tetris pieces
		container_texture = new Texture("container.png");
		container = new Container(container_texture);
		container.setPosition(x_padding,y_padding);

		game_over_sign = new Texture("game_over.png");

		//initializing textures for blocks
		red_block = new Texture("red_block.png");
		blue_block = new Texture("blue_block.png");
		orange_block = new Texture("orange_block.png");
		green_block = new Texture("green_block.png");
		yellow_block = new Texture("yellow_block.png");
		light_blue_block = new Texture("light_blue_block.png");
		purple_block = new Texture("purple_block.png");

		//initializing tetris board
		board = new Grid[15][10];

		//initializing upcoming list of blocks
		upcoming_blocks = new ArrayList<>();
		generate_upcoming_blocks();
		generate_upcoming_blocks();

		//initializing currently held tetris piece
		spawn_piece();

		time_passed = 0;
		game_over = false;
	}

	public void generate_upcoming_blocks(){
		template_list = Arrays.asList(new Color[]{Color.RED, Color.BLUE,
				Color.GREEN, Color.ORANGE, Color.YELLOW, Color.LIGHT_BLUE, Color.PURPLE});
		List<Color> temp = new ArrayList<>(template_list);
		Collections.shuffle(temp);
		upcoming_blocks.addAll(temp);
	}

	public boolean spawn_piece(){
		if(board[0][4] != null){
			current_piece = null;
			return false;
		}

		if(upcoming_blocks.size() < 7){
			generate_upcoming_blocks();
		}

		current_piece = new Piece(new int[]{0,4},
				upcoming_blocks.remove(0), board);
		return true;
	}

	public void update_board(int dt) throws Exception{
		if(game_over){
			return;
		}
		time_passed += dt;
		if(time_passed >= 90){
			//make the tetris block fall vertically a single grid
			if(!current_piece.move_down()){
				if(!spawn_piece()){
					game_over = true;
				}
			}
			time_passed = 0;
		}
	}
	public void handle_user_input() throws Exception{
		if(game_over){
			return;
		}
		//TODO: ADD SPACE BAR
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			current_piece.move_left();
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			current_piece.move_right();
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			if(!current_piece.move_down()){
				if(!spawn_piece()){
					game_over = true;
				}
			}

			time_passed = 0;
		}
	}

	@Override
	public void render (){
		ScreenUtils.clear(0, 0, 0, 1);
		try {
			update_board(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			handle_user_input();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		batch.begin();
		container.draw(batch);
		for(int i = 0; i < 15; i++){
			for(int j = 0; j < 10; j++){
				if(board[i][j] != null){
					Texture temp = red_block;
					switch(board[i][j].color){
						case BLUE:
							temp = blue_block;
							break;
						case GREEN:
							temp = green_block;
							break;
						case ORANGE:
							temp = orange_block;
							break;
						case YELLOW:
							temp = yellow_block;
							break;
						case LIGHT_BLUE:
							temp = light_blue_block;
							break;
						case PURPLE:
							temp = purple_block;
					}
					Sprite filler = new Sprite(temp);
					filler.setPosition(x_padding + j*44, y_padding + (14-i) * 44);
					filler.draw(batch);
				}
			}
		}
		if(game_over){
			new Sprite(game_over_sign).draw(batch);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		container_texture.dispose();
		red_block.dispose();
		blue_block.dispose();
		green_block.dispose();
		orange_block.dispose();
		yellow_block.dispose();
		light_blue_block.dispose();
		purple_block.dispose();
		game_over_sign.dispose();
	}
}
