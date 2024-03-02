# NOTES: This file is a translation of the Java tetris code that I have previously implemented in Python. I am doing so in order to create a "self-learning tetris AI" that incorporates 
# pytorch and DQN(deep Q networks) with CNN(convolutional neural networks). Of course, only the core logic previously implemented will be translated, and all the other "incompatible" parts
# will be replaced/substituted with python counterparts (for instance, game screen rendering).

import numpy as np
import torch
import random
from enum import Enum
import cv2
from PIL import Image
from matplotlib import style



class Tetris:
    rows = 22
    cols = 10

    def __init__(self):
        self.upcoming_blocks_visible_length = 6
        self.block_size = 15
    def restart(self):
        self.board = [[0] * self.rows for i in range(self.cols)]

if __name__ == "__main__":
    image = cv2.imread("..//assets/game_over.png")
    cv2.imshow("image",image)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

class Color(Enum):
    RED = 1
    BLUE = 2
    GREEN = 3
    ORANGE = 4
    YELLOW = 5
    LIGHT_BLUE = 6
    PURPLE = 7

class Piece:
    def __init__(self, location, color, board):
        self.location = location
        self.color = color
        self.board = board
        self.parts = self.get_parts(color)
        self.fill_grid()

    def get_parts(color):
        match color:
            case Color.RED:
                return [[-1,0],[0,-1],[1,-1],[0,0]]
            case Color.BLUE:
                return [[-1,0],[-2,0],[0,-1],[0,0]]
            case Color.ORANGE:
                return [[-1,0],[-2,0],[0,1],[0,0]]
            case Color.GREEN:
                return [[1,0],[0,-1],[-1,-1],[0,0]]
            case Color.YELLOW:
                return [[-1,0],[-1,1],[0,1],[0,0]]
            case Color.LIGHT_BLUE:
                return [[-1,0],[-2,0],[1,0],[0,0]]
            case _:
                return [[1,0],[0,1],[0,-1],[0,0]]

    def create_piece(self,location, color, board):
        parts = self.get_parts(color)
        for part in parts:
            x = location[0] + part[0]
            y = location[1] + part[1]
            if self.can_exist_here(board,x,y):
                return None
        return Piece(location, color, board)
    
    def can_exist_here(board, x, y):
        if x < 0 or x >= Tetris.rows or y < 0 or y >= Tetris.cols or board[x][y] == 1: # TODO: CHECK THIS CONDITION AGAIN
            return False
        return True

    def fill_grid():
        print("TODO")
    