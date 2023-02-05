package com.tetris.game;

public class Piece {
    int[] location;
    int[][] parts;
    Color color;
    Grid[][] board;
    private Piece(int[] location, Color color, Grid[][] board){
        this.location = location;
        this.color = color;
        this.board = board;
        this.parts = get_parts(color);
        fill_grid();
    }

    private static int[][] get_parts(Color color){
        int[][] parts;
        switch(color){
            case RED:
                parts = new int[][]{{-1,0}, {0,-1}, {1,-1}, {0,0}};
                break;
            case BLUE:
                parts = new int[][]{{-1,0},{-2,0},{0,-1}, {0,0}};
                break;
            case ORANGE:
                parts = new int[][]{{-1,0},{-2,0},{0,1}, {0,0}};
                break;
            case GREEN:
                parts = new int[][]{{1,0},{0,-1},{-1,-1}, {0,0}};
                break;
            case YELLOW:
                parts = new int[][]{{-1,0},{-1,1},{0,1}, {0,0}};
                break;
            case LIGHT_BLUE:
                parts = new int[][]{{-1,0},{-2,0},{1,0}, {0,0}};
                break;
            default:
                parts = new int[][]{{1,0},{0,1},{0,-1}, {0,0}};
        }
        return parts;
    }

    public static Piece create_piece(int[] location, Color color, Grid[][] board){
        int[][] parts = get_parts(color);

        for(int[] part: parts){
            int x = location[0] + part[0];
            int y = location[1] + part[1];
            if(!can_exist_here(board, x, y)){
                return null;
            }
        }

        return new Piece(location, color, board);
    }

    private static boolean can_exist_here(Grid[][] board, int x, int y){
        if(x < 0 || x >= TetrisGame.rows || y < 0 || y >= TetrisGame.cols || board[x][y] != null){
            return false;
        }
        return true;
    }

    private boolean can_rotate_clockwise(){
        for(int[] part: parts){
            int new_x, new_y;
            int x = part[0];
            int y = part[1];

            new_x = y;
            new_y = -1 * x;

            if(!can_exist_here(board, location[0]+new_x, location[1]+new_y)){
                return false;
            }
        }

        return true;
    }
    public boolean rotate_clockwise(){
        remove_grid();
        if(!can_rotate_clockwise()){
            fill_grid();
            return false;
        }

        for(int[] part: parts){
            int temp = part[0];
            part[0] = part[1];
            part[1] = temp * -1;
        }

        fill_grid();
        return true;
    }
    private boolean can_rotate_counterclockwise(){
        for(int[] part: parts){
            int new_x, new_y;
            int x = part[0];
            int y = part[1];

            new_x = -1 * y;
            new_y = x;

            if(!can_exist_here(board, location[0]+new_x, location[1]+new_y)){
                return false;
            }
        }

        return true;
    }
    public boolean rotate_counterclockwise(){
        remove_grid();
        if(!can_rotate_counterclockwise()){
            fill_grid();
            return false;
        }

        for(int[] part: parts){
            int temp = part[0];
            part[0] = -1 * part[1];
            part[1] = temp;
        }

        fill_grid();
        return true;
    }

    private void fill_grid(){
        for(int i = 0; i < parts.length; i++){
            int x = parts[i][0];
            int y = parts[i][1];
            board[location[0] + x][location[1] + y] = new Grid(color);
        }
    }

    private void remove_grid(){
        for(int i = 0; i < parts.length; i++){
            int x = parts[i][0];
            int y = parts[i][1];
            board[location[0] + x][location[1] + y] = null;
        }
    }

    private boolean can_move_down(){
        for(int i = 0; i < parts.length; i++){
            int x = parts[i][0] + 1;
            int y = parts[i][1];
            if(!can_exist_here(board, location[0]+x, location[1]+y)){
                return false;
            }
        }
        return true;
    }

    private boolean can_move_left(){
        for(int i = 0; i < parts.length; i++){
            int x = parts[i][0] ;
            int y = parts[i][1] - 1;
            if(!can_exist_here(board, location[0]+x, location[1]+y)){
                return false;
            }
        }
        return true;
    }

    private boolean can_move_right(){
        for(int i = 0; i < parts.length; i++){
            int x = parts[i][0];
            int y = parts[i][1] + 1;
            if(!can_exist_here(board, location[0]+x, location[1]+y)){
                return false;
            }
        }
        return true;
    }

    public boolean move_down(){
        remove_grid();
        if(!can_move_down()){
            fill_grid();
            return false;
        }

        this.location[0]++;
        fill_grid();
        return true;
    }

    public boolean move_left(){
        remove_grid();
        if(!can_move_left()){
            fill_grid();
            return false;
        }

        this.location[1]--;
        fill_grid();
        return true;
    }

    public boolean move_right(){
        remove_grid();
        if(!can_move_right()){
            fill_grid();
            return false;
        }

        this.location[1]++;
        fill_grid();
        return true;
    }

    public void delete_piece(){
        remove_grid();
    }
}
