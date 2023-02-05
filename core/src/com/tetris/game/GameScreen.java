package com.tetris.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.tetris.game.sprites.Container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    TetrisGame tetris_game;
    Texture container_texture;
    Container container;
    static final int rows = 22;
    static final int cols = 10;
    static final int upcoming_blocks_visible_length = 6;
    static final int block_size = 44;
    static final int visible_piece_size = 120;
    BitmapFont next_piece_font;
    BitmapFont saved_piece_font;
    Texture red_block;
    Texture blue_block;
    Texture orange_block;
    Texture green_block;
    Texture yellow_block;
    Texture light_blue_block;
    Texture purple_block;
    Texture game_over_sign;
    Texture red_piece;
    Texture blue_piece;
    Texture orange_piece;
    Texture green_piece;
    Texture yellow_piece;
    Texture light_blue_piece;
    Texture purple_piece;
    int time_passed;
    int press_timer;
    int total_time_passed;
    Grid[][] board;
    Piece current_piece;
    Color saved_piece;
    boolean game_over;
    final int x_padding = 20;
    final int y_padding = 15;
    List<Color> upcoming_blocks;
    List<Color> template_list;

    public GameScreen(TetrisGame tetris_game){
        this.tetris_game = tetris_game;
    }

    @Override
    public void show() {
        //setting up the container for tetris pieces
        container_texture = new Texture("board.png");
        container = new Container(container_texture);
        container.setPosition(x_padding * 2 + visible_piece_size,y_padding);

        game_over_sign = new Texture("game_over.png");

        //initializing textures for blocks and pieces
        red_block = new Texture("red_block.png");
        blue_block = new Texture("blue_block.png");
        orange_block = new Texture("orange_block.png");
        green_block = new Texture("green_block.png");
        yellow_block = new Texture("yellow_block.png");
        light_blue_block = new Texture("light_blue_block.png");
        purple_block = new Texture("purple_block.png");
        red_piece = new Texture("red_piece.png");
        blue_piece = new Texture("blue_piece.png");
        orange_piece = new Texture("orange_piece.png");
        green_piece = new Texture("green_piece.png");
        yellow_piece = new Texture("yellow_piece.png");
        light_blue_piece = new Texture("light_blue_piece.png");
        purple_piece = new Texture("purple_piece.png");

        //initializing tetris board
        board = new Grid[rows][cols];

        //initializing upcoming list of blocks
        upcoming_blocks = new ArrayList<>();
        generate_upcoming_blocks();
        generate_upcoming_blocks();
        next_piece_font = new BitmapFont();
        saved_piece_font = new BitmapFont();

        //initializing currently held tetris piece
        spawn_piece();

        time_passed = 0;
        press_timer = 0;
        total_time_passed = 0;
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
        current_piece = Piece.create_piece(new int[]{2,4},
                upcoming_blocks.remove(0), board);
        if(current_piece == null){
            return false;
        }

        if(upcoming_blocks.size() < 7){
            generate_upcoming_blocks();
        }

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
                clear_possible_lines();
                if(!spawn_piece()){
                    game_over = true;
                }
            }
            time_passed = 0;
        }
    }

    public void clear_possible_lines(){
        for(int i = 0; i < rows; i++){
            boolean gap_exists = false;
            for(int j = 0; j < cols; j++){
                if(board[i][j] == null){
                    gap_exists = true;
                    break;
                }
            }
            if(gap_exists){
                continue;
            }
            delete_row(i);
        }
    }
    private void delete_row(int row){
        for(int i = 0; i < cols; i++){
            board[row][i] = null;
        }

        for(int i = row; i > 0; i--){
            for(int j = 0; j < cols; j++){
                board[i][j] = board[i-1][j];
            }
        }
        for(int i = 0; i < cols; i++){
            board[0][i] = null;
        }
    }

    private boolean save_piece(){
        if(saved_piece == null){
            saved_piece = current_piece.color;
            current_piece.delete_piece();
            return spawn_piece();
        }
        else{
            Color temp = saved_piece;
            saved_piece = current_piece.color;
            current_piece.delete_piece();
            current_piece = Piece.create_piece(new int[]{2,4}, temp, board);
            return current_piece != null;
        }
    }

    public void handle_user_input() throws Exception{
        if(game_over){
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                game_over = false;

                for(int i = 0; i < rows; i++){
                    for(int j = 0; j < cols; j++){
                        board[i][j] = null;
                    }
                }
                time_passed = 0;
                press_timer = 0;
                upcoming_blocks = new ArrayList<>();
                generate_upcoming_blocks();
                generate_upcoming_blocks();
                spawn_piece();
            }
            return;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            current_piece.move_left();
            press_timer = 0;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            current_piece.move_right();
            press_timer = 0;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            if(!current_piece.move_down()){
                clear_possible_lines();
                if(!spawn_piece()){
                    game_over = true;
                }
            }
            time_passed = 0;
            press_timer = 0;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            while(current_piece.move_down()){
                current_piece.move_down();
            }
            clear_possible_lines();
            if(!spawn_piece()){
                game_over = true;
            }
            time_passed = 0;
            press_timer = 0;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){// Counter clockwise
            current_piece.rotate_counterclockwise();
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){// Clockwise
            current_piece.rotate_clockwise();
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
            boolean temp = save_piece();
            if(!temp){
                game_over = true;
            }
            time_passed = 0;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            press_timer++;
            if(press_timer > 9){
                current_piece.move_left();
                press_timer -= 3;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            press_timer++;
            if(press_timer > 9){
                current_piece.move_right();
                press_timer -= 3;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            press_timer++;
            if(press_timer > 9){
                if(!current_piece.move_down()){
                    clear_possible_lines();
                    if(!spawn_piece()){
                        game_over = true;
                    }
                    press_timer = 3;
                }
                time_passed = 0;
                press_timer -= 3;
                if(current_piece.is_touching_ground()){
                    System.out.println(current_piece.is_touching_ground());
                    press_timer -= 9;
                }
            }
        }
    }

    @Override
    public void render(float dt){
        //setting delay before very first input
        total_time_passed++;
        if(total_time_passed > 500000){
            total_time_passed = 3;
        }
        ScreenUtils.clear(0, 0, 0, 1);
        try {
            update_board(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            if(total_time_passed > 2){
                handle_user_input();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        tetris_game.batch.begin();
        container.draw(tetris_game.batch);
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
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
                    filler.setPosition(x_padding * 2 + visible_piece_size + j*block_size, y_padding + (rows-1-i) * block_size);
                    filler.draw(tetris_game.batch);
                }
            }
        }
        for(int i = 0; i < upcoming_blocks.size() && i <= upcoming_blocks_visible_length; i++){
            Color color = upcoming_blocks.get(i);
            Texture temp = get_block_from_color(color);
            Sprite upcoming_block = new Sprite(temp);
            upcoming_block.setSize(visible_piece_size, visible_piece_size);
            upcoming_block.setPosition(x_padding * 3 + visible_piece_size + cols * block_size,
                    y_padding + (upcoming_blocks_visible_length - i) * visible_piece_size);
            upcoming_block.draw(tetris_game.batch);
        }
        next_piece_font.draw(tetris_game.batch, "Next Pieces", x_padding * 4 + visible_piece_size + cols * block_size,
                y_padding + upcoming_blocks_visible_length * visible_piece_size + 150);


        if(saved_piece != null){
            Texture temp_texture = get_block_from_color(saved_piece);
            Sprite temp_sprite = new Sprite(temp_texture);
            temp_sprite.setSize(visible_piece_size, visible_piece_size);
            temp_sprite.setPosition(x_padding, y_padding + upcoming_blocks_visible_length * visible_piece_size);
            temp_sprite.draw(tetris_game.batch);
        }
        saved_piece_font.draw(tetris_game.batch, "Saved Piece", x_padding * 2,
                y_padding + upcoming_blocks_visible_length * visible_piece_size + 150);

        if(game_over){
            new Sprite(game_over_sign).draw(tetris_game.batch);
        }
        tetris_game.batch.end();
    }

    private Texture get_block_from_color(Color color){
        Texture temp = red_piece;
        switch(color){
            case BLUE:
                temp = blue_piece;
                break;
            case GREEN:
                temp = green_piece;
                break;
            case ORANGE:
                temp = orange_piece;
                break;
            case YELLOW:
                temp = yellow_piece;
                break;
            case LIGHT_BLUE:
                temp = light_blue_piece;
                break;
            case PURPLE:
                temp = purple_piece;
        }
        return temp;
    }

    @Override
    public void dispose () {
        tetris_game.batch.dispose();
        container_texture.dispose();
        red_block.dispose();
        blue_block.dispose();
        green_block.dispose();
        orange_block.dispose();
        yellow_block.dispose();
        light_blue_block.dispose();
        purple_block.dispose();
        game_over_sign.dispose();
        red_piece.dispose();
        blue_piece.dispose();
        green_piece.dispose();
        orange_piece.dispose();
        yellow_piece.dispose();
        light_blue_piece.dispose();
        purple_piece.dispose();
        next_piece_font.dispose();
        saved_piece_font.dispose();
    }
}
