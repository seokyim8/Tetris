from Tetris import Tetris
import torch
import time
import cv2

LOAD_PATH = "./Trained_Models" 
MODEL_NAME = "/final_model"
screen_refresh_rate = 120

def test():
    torch.manual_seed(256)
    model = torch.load(LOAD_PATH + MODEL_NAME)

    model.eval()
    environment = Tetris()
    environment.restart()
    
    output = cv2.VideoWriter("model_output.mp4", cv2.VideoWriter_fourcc(*"MJPG"),
                             screen_refresh_rate, (int(1.5 * Tetris.rows * Tetris.block_size), Tetris.cols * Tetris.block_size))
    while True:
        next_steps = environment.get_next_states()

        next_actions, next_states = zip(*next_steps.items())
        next_states = torch.stack(next_states)
        predictions = model(next_states)[:, 0]
        index = torch.argmax(predictions).item()
        action = next_actions[index]

        reward, terminal = environment.take_action(action, True, output)

        # For better viewing experience # 
        # time.sleep(0.3)

        if terminal:
            output.release()
            break
        


### MAIN FUNCTION ###
if __name__ == "__main__":
    test()