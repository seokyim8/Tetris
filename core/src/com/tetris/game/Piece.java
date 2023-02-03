package com.tetris.game;

public class Piece {
    int[] location;
    int[] parts;
    Color color;
    Grid[][] board;
    public Piece(int[] location, Color color, Grid[][] board){
        this.location = location;
        this.color = color;
        this.board = board;
        fill_grid();
    }

    private void fill_grid(){
        //TODO: parts not taken into account
        board[location[0]][location[1]] = new Grid(color);
    }

    private void remove_grid(){
        board[location[0]][location[1]] = null;
    }

    private boolean can_move_down(){
        //simple version for a single-block tetrimino
        int x = location[0];
        int y = location[1];
        if(x+1 < 15 && board[x+1][y] == null) {
            return true;
        }
        return false;
    }

    private boolean can_move_left(){
        //simple version for a single-block tetrimino
        int x = location[0];
        int y = location[1];
        if(y-1 >= 0 && board[x][y-1] == null) {
            return true;
        }
        return false;
    }

    private boolean can_move_right(){
        //simple version for a single-block tetrimino
        int x = location[0];
        int y = location[1];
        if(y+1 < 10 && board[x][y+1] == null) {
            return true;
        }
        return false;
    }


    //TODO: MANAGE GRID OBJECTS, AND PART IS NOT INCLUDED YET
    public boolean move_down(){
        if(!can_move_down()){
            return false;
        }

        remove_grid();
        this.location[0]++;
        fill_grid();
        return true;
    }

    public boolean move_left(){
        if(!can_move_left()){
            return false;
        }

        remove_grid();
        this.location[1]--;
        fill_grid();
        return true;
    }

    public boolean move_right(){
        if(!can_move_right()){
            return false;
        }

        remove_grid();
        this.location[1]++;
        fill_grid();
        return true;
    }
}
