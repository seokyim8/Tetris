# NOTES: This file is a translation of the Java tetris code that I have previously implemented in Python. I am doing so in order to create a "self-learning tetris AI" that incorporates 
# pytorch and DQN(deep Q networks) with CNN(convolutional neural networks). Of course, only the core logic previously implemented will be translated, and all the other "incompatible" parts
# will be replaced/substituted with python counterparts (for instance, game screen rendering).

import numpy as np
import random
from enum import Enum
import cv2
from PIL import Image
from matplotlib import style


class Tetris:
    rows = 10
    cols = 22
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
        self.saved_peice = None # IS A COLOR BTW; A COLOR IS SAVED TO INDICATE THE TYPE OF BLOCK
        self.current_piece = None
        self.generate_upcoming_blocks()
        self.generate_upcoming_blocks()
        self.spawn_piece()
        self.game_over = False
        self.tetriminos = 0

        # Used for game screen rendering
        self.additional_board = np.ones((self.cols * self.block_size, self.rows * int(self.block_size / 2), 3), dtype=np.uint8) * np.array([204, 204, 255], dtype=np.uint8)
        self.text_color_val = (200, 20, 220)

        # Initializing fields used for AI training
        self.score = 0
        self.cleared_lines = 0

    
    def restart(self):
        self.board = [[None] * Tetris.rows for _ in range(Tetris.cols)]
        self.score = 0
        self.cleared_lines = 0
        self.upcoming_blocks = []
        self.generate_upcoming_blocks()
        self.generate_upcoming_blocks()
        self.spawn_piece()
        self.game_over = False
        self.tetriminos = 0
        
    def generate_upcoming_blocks(self):
        temp = [Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.LIGHT_BLUE, Color.PURPLE]
        random.shuffle(temp)
        self.upcoming_blocks.extend(temp)

    def spawn_piece(self):
        temp_piece = Piece([2,4], self.upcoming_blocks.pop(0), self.board)
        if not temp_piece.verified_piece():
            return False
        
        if len(self.upcoming_blocks) < 7:
            self.generate_upcoming_blocks()

        self.current_piece = temp_piece
        return True
    
    def update_board(self):
        if self.game_over:
            return

    def clear_possible_lines(self):
        for i in range(Tetris.cols):
            gap_exists = False
            for j in range(Tetris.rows):
                if self.board[i][j] == None:
                    gap_exists = True
                    break
            if gap_exists:
                continue
            self.delete_row(i)
            self.cleared_lines += 1
    
    def delete_row(self, row_index):
        for i in range(Tetris.rows):
            self.board[row_index][i] = None
        
        for i in range(row_index, 0, -1):
            for j in range(Tetris.rows):
                self.board[i][j] = self.board[i-1][j]
        
        for i in range(Tetris.rows):
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
            temp_piece = Piece([2,4], temp, self.board)
            if not temp_piece.verified_piece():
                return False
        
            self.current_piece = temp_piece
            return True
        
    def process_input(self, response):
        if response == 'd':
            if not self.current_piece.move_down():
                self.clear_possible_lines()
                if not self.spawn_piece():
                    self.game_over = True
        elif response == 'l':
            self.current_piece.move_left()
        elif response == 'r':
            self.current_piece.move_right()
        elif response == 'rc':
            self.current_piece.rotate_clockwise()
        elif response == 'rcc':
            self.current_piece.rotate_counterclockwise()
        elif response == 's':
            temp = self.save_piece()
            if not temp:
                self.game_over = True
        elif response == 'blanks':
            print(self.blanks())
        elif response == 'max h':
            print(self.max_height())
        elif response == 'disparity':
            print(self.overall_height_disparity())
        
        self.render()

    def to_RGB(self, color):
        match color:
            case Color.RED:
                return (254, 151, 32)
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
            case Color.PURPLE:
                return (0, 0, 255)
            case None:
                return (0,0,0)

    # Followed an online guide for specific rendering processes; subject to future change
    def render(self, video=None): 
        # Rendering board; assinging RGB values based on Color ENUM values
        img = [self.to_RGB(p) for row in self.board for p in row]
        img = np.array(img).reshape((self.cols, self.rows, 3)).astype(np.uint8)
        img = img[..., ::-1]
        
        # Rescaling process
        img = Image.fromarray(img, "RGB")
        img = img.resize((self.rows * self.block_size, self.cols * self.block_size), 0)
        img = np.array(img)
        img[[i * self.block_size for i in range(self.cols)], :, :] = 0
        img[:, [i * self.block_size for i in range(self.rows)], :] = 0
        img = np.concatenate((img, self.additional_board), axis=1)


        # Rendering Score, Pieces, and Lines with their actual values
        cv2.putText(img, "Score:", (self.rows * self.block_size + int(self.block_size / 2), self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=0.5, color=self.text_color_val)
        cv2.putText(img, str(self.score),
                    (self.rows * self.block_size + int(self.block_size / 2), 2 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=0.5, color=self.text_color_val)

        cv2.putText(img, "Pieces:", (self.rows * self.block_size + int(self.block_size / 2), 4 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=0.5, color=self.text_color_val)
        cv2.putText(img, str(self.tetriminos),
                    (self.rows * self.block_size + int(self.block_size / 2), 5 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=0.5, color=self.text_color_val)

        cv2.putText(img, "Lines:", (self.rows * self.block_size + int(self.block_size / 2), 7 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=0.5, color=self.text_color_val)
        cv2.putText(img, str(self.cleared_lines),
                    (self.rows * self.block_size + int(self.block_size / 2), 8 * self.block_size),
                    fontFace=cv2.FONT_HERSHEY_DUPLEX, fontScale=0.5, color=self.text_color_val)

        # Upon request, video is recorded; Primarily used when testing
        if video:
            video.write(img)

        cv2.imshow("DQN TETRIS GAMEPLAY", img)
        cv2.waitKey(1)
        return



    ######################################################
    # FUNCTIONS RELATING TO DQN #

    # Note: I decided to calculate/pass the number of blank spaces in between occupied grid cells, 
    # height disaprity between adjacent columns and max height as the states to my Deep Q learning 
    # model. The following functions are directly related to and/or facilitate processing the 
    # aformentioned values.

    # TODO: CREATING the functions below for DQN:

    def get_next_states(self):
        # TODO: FINISH
        return
    
    def take_action(self):
        # TODO: FINISH
        return
    
    def blanks(self):
        # If there are occupied grid cells above an empty cell, then that is a blank/hole
        total_sum = 0
        empty = False
        temp_sum = 0
        for i in range(Tetris.rows):
            for j in range(20,-1,-1):
                if empty:
                    empty = True
                    temp_sum += 1
                else:
                    empty = False
                    total_sum += temp_sum
                    temp_sum = 0
        return total_sum
    
    def overall_height_disparity(self):
        # I define the overall height disaprity to be the sum of the differences in height between adjacent columns
        heights = []
        for i in range(Tetris.rows):
            for j in range(Tetris.cols):
                if self.board[j][i] != None:
                    heights.append(Tetris.cols - j)
                    break

        disparity = 0
        for i in range(len(heights)-1):
            disparity += abs(heights[i+1] - heights[i])
        return disparity
    
    def max_height(self):
        heights = []
        for i in range(Tetris.rows):
            for j in range(Tetris.cols):
                if self.board[j][i] != None:
                    heights.append(Tetris.cols - j)
                    break

        return max(heights)


    ######################################################



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


    def verified_piece(self):
        parts = self.get_parts(self.color)
        for part in parts:
            x = self.location[0] + part[0]
            y = self.location[1] + part[1]
            if not self.can_exist_here(self.board,x,y):
                return False
        self.fill_grid()
        return True


    def can_exist_here(self, board, x, y):
        if x < 0 or x >= Tetris.cols or y < 0 or y >= Tetris.rows or board[x][y] != None:
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

            if not self.can_exist_here(self.board, self.location[0] + new_x, self.location[1] + new_y):
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
            x = part[0]
            y = part[1]

            new_x = -1 * y
            new_y = x

            if not self.can_exist_here(self.board, self.location[0] + new_x, self.location[1] + new_y):
                return False

        return True
    
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
            self.board[self.location[0] + x][self.location[1] + y] = self.color 

    def remove_grid(self):
        for i in range(len(self.parts)):
            x = self.parts[i][0]
            y = self.parts[i][1]
            self.board[self.location[0] + x][self.location[1] + y] = None

    def can_move_down(self):
        for i in range(len(self.parts)):
            x = self.parts[i][0] + 1
            y = self.parts[i][1]
            if not self.can_exist_here(self.board, self.location[0] + x, self.location[1] + y):
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
            self.fill_grid()
            return False
        
        self.location[0] += 1
        self.fill_grid()
        return True
    
    def move_left(self):
        self.remove_grid()
        if not self.can_move_left():
            self.fill_grid()
            return False
        
        self.location[1] -= 1
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
    


# For those want to play it yourself, utilize the following main function:
if __name__ == "__main__":
    style.use("ggplot")
    game = Tetris()
    game.restart()
    while True:
        response = input("Type d,l, or r.")
        game.process_input(response)
        if game.game_over:
            game.restart()