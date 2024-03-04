#### Creator: Seok Yim (Noah)

# Project Summary: Tetris project with self-learning AI

This project is an attempt to "resurrect" my previously attempted Tetris game application by adding a "self-learning AI" feature. All of the Java code used for building the Tetris game has been translated 
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
  
