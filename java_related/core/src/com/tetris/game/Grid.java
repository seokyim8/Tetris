package com.tetris.game;

enum Color {RED, BLUE, GREEN, ORANGE, YELLOW, LIGHT_BLUE, PURPLE};

public class Grid {
    Color color;

    public Grid(Color color){
        this.color = color;
    }
}
