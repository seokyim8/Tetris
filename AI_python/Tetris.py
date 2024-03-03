# NOTES: This file is a translation of the Java tetris code that I have previously implemented in Python. I am doing so in order to create a "self-learning tetris AI" that incorporates 
# pytorch and DQN(deep Q networks) with CNN(convolutional neural networks). Of course, only the core logic previously implemented will be translated, and all the other "incompatible" parts
# will be replaced/substituted with python counterparts (for instance, game screen rendering).

import numpy as np
import random
from enum import Enum
import cv2
from PIL import Image


class Tetris:
    rows = 22
    cols = 10
    upcoming_blocks_visible_length = 6
    block_size = 20
    visible_piece_size = 120
    x_padding = 20
    y_padding = 15

    def __init__(self):
        # Initializing game-related fields
        self.upcoming_blocks_visible_length = 6
        self.block_size = 15
        self.board = [[None] * Tetris.rows for _ in range(Tetris.cols)]
        self.upcoming_blocks = []
        self.saved_peice = None
        self.current_piece = None
        self.generate_upcoming_blocks()
        self.generate_upcoming_blocks()
        self.spawn_piece()
        self.game_over = False

        # Used for game screen rendering
        self.additional_board = np.ones((self.cols * self.block_size, self.rows * int(self.block_size / 2), 3), dtype=np.uint8) * np.array([204, 204, 255], dtype=np.uint8)
        self.text_color_val = (200, 20, 220)

        # Initializing fields used for AI training
        self.score = 0
        self.cleared_lines = 0

    def generate_upcoming_blocks(self):
        temp = [Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.LIGHT_BLUE, Color.PURPLE]
        random.shuffle(temp)
        self.upcoming_blocks.extend(temp)

    def spawn_piece(self):
        self.current_piece = Piece([2,4], self.upcoming_blocks.pop(0), self.board)
        if self.current_piece == None:
            return False
        
        if len(self.upcoming_blocks) < 7:
            self.generate_upcoming_blocks()

        return True
    
    def update_board(self):
        if self.game_over:
            return

    def clear_possible_lines(self):
        for i in range(Tetris.rows):
            gap_exists = False
            for j in Tetris.cols:
                if self.board[i][j] == None:
                    gap_exists = True
                    break
            if gap_exists:
                continue
            self.delete_row()
            self.cleared_lines += 1
    
    def delete_row(self, row_index):
        for i in range(Tetris.cols):
            self.board[row_index][i] = None
        
        for i in range(row_index, 0, -1):
            for j in range(Tetris.cols):
                self.board[i][j] = self.board[i-1][j]
        
        for i in range(Tetris.cols):
            self.board[0][i] = None

    def save_piece(self):
        if self.saved_peice == None:
            self.saved_peice = self.current_piece.color
            self.current_piece.delete_piece()
            return self.spawn_piece()
        else:
            temp = self.saved_peice
            self.saved_peice = self.current_piece.color
            self.current_piece.delete_piece()
            self.current_piece = Piece([2,4], temp, self.board)
            return self.current_piece != None
        
    def process_input(self, response):
        if response == 'd':
            self.current_piece.move_left()
        self.render()

    def to_RGB(self, color):
        match color:
            case Color.RED:
                return (0, 0, 255)
            case Color.BLUE:
                return (255, 255, 0)
            case Color.ORANGE:
                return (147, 88, 254)
            case Color.GREEN:
                return (54, 175, 144)
            case Color.YELLOW:
                return (255, 0, 0)
            case Color.LIGHT_BLUE:
                return (102, 217, 238)
            case _:
                return (254, 151, 32)

    def render(self, video=None):
        if self.gameover == False:
            img = [self.to_RGB(p) for row in self.board for p in row]
        else:
            img = [self.to_RGB(p) for row in self.board for p in row]
        img = np.array(img).reshape((self.cols, self.rows, 3)).astype(np.uint8)
        img = img[..., ::-1]
        img = Image.fromarray(img, "RGB")

        img = img.resize((self.rows * self.block_size, self.cols * self.block_size), 0)
        img = np.array(img)
        img[[i * self.block_size for i in range(self.cols)], :, :] = 0
        img[:, [i * self.block_size for i in range(self.rows)], :] = 0


        img = np.concatenate((img, self.additional_board), axis=1)


        cv2.putText(img, "Score:", (self.rows * self.block_size + int(self.block_size / 2), self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=1.0, color=self.text_color_val)
        cv2.putText(img, str(self.score),
                    (self.rows * self.block_size + int(self.block_size / 2), 2 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=1.0, color=self.text_color_val)

        cv2.putText(img, "Pieces:", (self.rows * self.block_size + int(self.block_size / 2), 4 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=1.0, color=self.text_color_val)
        cv2.putText(img, str(self.tetrominoes),
                    (self.rows * self.block_size + int(self.block_size / 2), 5 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=1.0, color=self.text_color_val)

        cv2.putText(img, "Lines:", (self.rows * self.block_size + int(self.block_size / 2), 7 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=1.0, color=self.text_color_val)
        cv2.putText(img, str(self.cleared_lines),
                    (self.rows * self.block_size + int(self.block_size / 2), 8 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=1.0, color=self.text_color_val)

        if video:
            video.write(img)

        cv2.imshow("Deep Q-Learning Tetris", img)
        cv2.waitKey(1)

        return


    def restart(self):
        self.board = [[None] * Tetris.rows for _ in range(Tetris.cols)]
        self.score = 0
        self.cleared_lines = 0
        self.upcoming_blocks = []
        self.generate_upcoming_blocks()
        self.generate_upcoming_blocks()
        self.game_over = False




######################################################
        

class Color(Enum):
    RED = 1
    BLUE = 2
    GREEN = 3
    ORANGE = 4
    YELLOW = 5
    LIGHT_BLUE = 6
    PURPLE = 7


######################################################
    



class Piece:
    def __init__(self, location, color, board):
        self.location = location
        self.color = color
        self.board = board
        self.parts = self.get_parts(color)
        self.fill_grid()

    def get_parts(self, color):
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


    # USELESS FUNCTION
    def create_piece(self, location, color, board):
        parts = self.get_parts(color)
        for part in parts:
            x = location[0] + part[0]
            y = location[1] + part[1]
            if self.can_exist_here(board,x,y):
                return None
        return Piece(location, color, board)
    # ENDS HERE


    def can_exist_here(board, x, y):
        if x < 0 or x >= Tetris.rows or y < 0 or y >= Tetris.cols or board[x][y] != None: # TODO: CHECK THIS CONDITION AGAIN
            return False
        return True

    def can_rotate_clockwise(self):
        for part in self.parts:
            new_x = -1
            new_y = -1
            x = part[0]
            y = part[1]

            new_x = y
            new_y = -1 * x

            if self.can_exist_here(self.board, self.location[0], + new_x, self.location[1] + new_y):
                return False
 
        return True

    def rotate_clockwise(self):
        self.remove_grid()
        if not self.can_rotate_clockwise():
            self.filld_grid()
            return False
        
        for part in self.parts:
            temp = part[0]
            part[0] = part[1]
            part[1] = temp * -1

        self.fill_grid()
        return True
    
    def can_rotate_counterclockwise(self):
        for part in self.parts:
            new_x = -1
            new_y = -1
            x = self.part[0]
            y = self.part[1]

            new_x = -1 * y
            new_y = x

            if not self.can_exist_here(self.board, self.location[0] + new_x, self.location[1] + new_y):
                return False

    def rotate_counterclockwise(self):
        self.remove_grid()
        if not self.can_rotate_counterclockwise():
            self.fill_grid()
            return False        
        for part in self.parts:
            temp = part[0]
            part[0] = -1 * part[1]
            part[1] = temp
        
        self.fill_grid()
        return True
    
    def fill_grid(self):
        for i in range(len(self.parts)):
            x = self.parts[i][0]
            y = self.parts[i][1]
            self.board[self.location[0] + x][self.location[1] + y] = self.color # TODO: CHECK THIS LATER

    def remove_grid(self):
        for i in range(len(self.parts)):
            x = self.parts[i][0]
            y = self.parts[i][1]
            self.board[self.location[0] + x][self.location[1] + y] = None

    def can_move_down(self):
        for i in range(len(self.parts)):
            x = self.parts[i][0] + 1
            y = self.parts[i][1]
            if not self.can_exist_here(self.board, self.location[0] + x, self.lcoation[1] + y):
                return False
        return True
    
    def can_move_left(self):
        for i in range(len(self.parts)):
            x = self.parts[i][0]
            y = self.parts[i][1] -1
            if not self.can_exist_here(self.board, self.location[0] + x, self.location[1] + y):
                return False
        return True
    
    def can_move_right(self):
        for i in range(len(self.parts)):
            x = self.parts[i][0]
            y = self.parts[i][1] + 1
            if not self.can_exist_here(self.board, self.location[0] + x, self.location[1] + y):
                return False
        return True
    
    def move_down(self):
        self.remove_grid()
        if not self.can_move_down():
            self.filld_grid()
            return False
        
        self.location[0] += 1
        self.fill_grid()
        return True
    
    def move_left(self):
        self.remove_grid()
        if not self.can_move_left():
            self.fill_grid()
            return False
        
        self.lcoation[1] -= 1
        self.fill_grid()
        return True
    
    def move_right(self):
        self.remove_grid()
        if not self.can_move_right():
            self.fill_grid()
            return False
        
        self.location[1] += 1
        self.fill_grid()
        return True
    
    def delete_piece(self):
        self.remove_grid()
    
    def is_touching_ground(self):
        self.removeC_grid()
        for part in self.parts:
            if not self.can_exist_here(self.board, self.location[0] + part[0] + 1, self.location[1] + part[1]):
                self.fill_grid()
                return True
        self.fill_grid()
        return False
    


if __name__ == "__main__":
    game = Tetris()
    game.restart()
    while True:
        response = input("Type d,l, or r.")
        game.process_input(response)