#### Creator: Seok Yim (Noah)

# Project: Self-learning Tetris AI #

## Model with almost no training vs with sufficient training
![model_500-ezgif com-resize](https://github.com/seokyim8/Tetris/assets/49558316/2334edb3-d3af-4d02-a010-c4b349faee1a)&nbsp&nbsp&nbspversus&nbsp&nbsp&nbsp![model_2000-ezgif com-crop](https://github.com/seokyim8/Tetris/assets/49558316/3b73e199-0e03-430c-8867-6bb577f1ce48)


# Project Summary: Tetris project with self-learning AI using DQN

This project is an attempt to "resurrect" my previously attempted Tetris game application by adding a "self-learning AI" feature. All of the Java code used for building my Tetris game has been translated 
to Python (by hand), and additional libraries (like Pytorch) were used to enable Deep Q Learning for the Tetris bot. Details are listed below:

### Method Used: Deep Q Learning with neural networks.
    - NOTE: Most of the following values/strategies were arbitrarily chosen; hence, they are subject to change upon finding a better model-developing strategy! -
    number of layers: 3
    learning rate: 0.001
    activation function: ReLU
    epsilon-greedy strategy: linear (for simplicity)
        epsilon decay formula: EPSILON_DELTA = (INITIAL_EPSILON - FINAL_EPSILON) / MAX_EPISODE
    max number of iterations/episodes: 2000 
    gamma(discount factor/rate) = 0.99
    batch size = 512

### Game screen rendering: handled through opencv (referred to online guides/codes; hence, it may not be the best practice).
    output format of testing: .mp4

### Game Logic: Taken from my previous Java code.
    game mechanics: classic(old) Tetris
    t-spin: not available
    saving functionality: available for human-driven gameplay, not available for AI

### How to access/view/use the AI:
    1. Go into AI_python directory
    2. Run "python Test.py" (you can set MODEL_NAME to any other model contained in Trained_Models directory)

### How to train the AI:
    1. Go into AI_python directory
    2. Run "python Train.py" (you can tweak the parameters for possibly better results)
    3. If desired, you can modify the DQN network structure by modifying DQN.py
    4. If desired, you can enable cuda operation with pytorch to speed up the process (Check availability through "torch.cuda.is_available()") 
