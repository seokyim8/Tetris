# NOTES: This file is a translation of the Java tetris code that I have previously implemented in Python. I am doing so in order to create a "self-learning tetris AI" that incorporates 
# pytorch and DQN(deep Q networks) with CNN(convolutional neural networks). Of course, only the core logic previously implemented will be translated, and all the other "incompatible" parts
# will be replaced/substituted with python counterparts (for instance, game screen rendering).

import numpy as np
import torch
import random

from PIL import Image
import cv2
from matplotlib import style



class Tetris:
    def __init__(self):
        self.rows = 22
        self.cols = 10
        self.upcoming_blocks_visible_length = 6
        self.block_size = 15
    def restart(self):
        self.board = [[0] * self.rows for i in range(self.cols)]