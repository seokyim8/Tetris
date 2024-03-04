from DQN import DQN
from Tetris import Tetris
import torch
import torch.nn as nn
from random import random, randint, sample
from collections import deque
import numpy as np


# Preset of Parameters/Hyperparameters; TODO: Arbitrarily selected for now:
LEARNING_RATE = 0.001
REPLAY_MEM_SIZE = 25000
MAX_EPISODE = 2000
INITIAL_EPSILON = 1
FINAL_EPSILON = 0.001
EPSILON_DELTA = (INITIAL_EPSILON - FINAL_EPSILON)/MAX_EPISODE
BATCH_SIZE = 512
GAMMA = 0.99
SAVE_INTERVAL = 50
SAVE_PATH = "./Trained_Models" 

def train():
    torch.manual_seed(256)
    env = Tetris()
    model = DQN()

    # NN using Adam Optimization; NOTE: could try using SGD as well?
    optimizer = torch.optim.Adam(model.parameters(), lr=LEARNING_RATE)
    loss_function = nn.MSELoss() # Using Q-error/Q-loss

    state = env.restart()
    episode = 0

    # Setting up replay memory for the model to "learn" from experience
    replay_memory = deque(maxlen = REPLAY_MEM_SIZE)
    epsilon = INITIAL_EPSILON

    while episode < MAX_EPISODE:
        next_action_to_state = env.get_next_states()

        # Setting up epsilon-greedy strategy: Linear Decay for simplicity
        epsilon -= EPSILON_DELTA

        # Exploration vs Exploitation: 
        next_actions, next_states = zip(*next_action_to_state.items())
        next_states = torch.stack(next_states)

        # Calculating/predicting next state Q values (random vs optimal)
        model.eval()
        with torch.no_grad():
            predictions = model(next_states)[:, 0]
        model.train()


        if random() < epsilon:
            selected = randint(0, len(next_action_to_state) - 1)
        else:
            selected = torch.argmax(predictions).item()


        next_state = next_states[selected, :]
        action = next_actions[selected]
        reward, terminal = env.take_action(action, True, None)

        # Filling in replay memory for the models' future use
        replay_memory.append([state, reward, next_state, terminal])

        # Checking if the AI died
        if terminal:
            final_score = env.score
            final_tetriminos = env.tetriminos
            final_cleared_lines = env.cleared_lines
            state = env.restart()
        else:
            state = next_state
            continue

        # Continue episode if replay memory not filled up enough
        if len(replay_memory) < REPLAY_MEM_SIZE / 10:
            continue


        # Retreiving replay memory at random; learning from the past, basically
        batch = sample(replay_memory, min(len(replay_memory), BATCH_SIZE))

        # Debugging
        # print(batch)
        #

        state_batch, reward_batch, next_state_batch, terminal_batch = zip(*batch)
        state_batch = torch.stack(tuple(state for state in state_batch))
        reward_batch = torch.from_numpy(np.array(reward_batch, dtype=np.float32)[:, None])
        next_state_batch = torch.stack(tuple(state for state in next_state_batch))


        q_estimates = model(state_batch)

        model.eval()
        with torch.no_grad():
            next_prediction_batch = model(next_state_batch)
        model.train()

        target = torch.cat(tuple(reward if terminal else reward + GAMMA * prediction for reward, terminal, prediction in
                  zip(reward_batch, terminal_batch, next_prediction_batch)))[:, None]

        # Backpropagation
        optimizer.zero_grad()
        loss = loss_function(q_estimates, target)
        loss.backward()
        optimizer.step()


        # Marks the end of the current episode
        episode += 1

        # Summarizing results:
        print("Episode: ", episode, "  out of ", MAX_EPISODE)
        print("Taken Action: ", action, "   Score: ", final_score, "    Tetriminos Placed: ", final_tetriminos)
        print("Total Lines Cleared: ", final_cleared_lines)


        # Consistent saving 
        if episode > 0 and episode % SAVE_INTERVAL == 0:
            torch.save(model, SAVE_PATH + "/model_" + str(episode))

    torch.save(model, SAVE_PATH + "/final_model")



### MAIN FUNCTION ###
if __name__ == "__main__":
    train()
