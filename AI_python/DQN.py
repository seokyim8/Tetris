# Deep Q Learning Model (or simply DQN) #

import torch.nn as nn

class DQN(nn.Module):
    def __init__(self):
        super(DQN, self).__init__()

        self.activation = nn.ReLU()

        # Utilizing nn.Linear() functions' default weight initialization process: Kaiming uniform initialization
        self.layer1 = nn.Linear(in_features = 4, out_features = 56, bias = True)
        self.layer2 = nn.Linear(in_features = 56, out_features = 56, bias = True)
        self.layer3 = nn.Linear(in_features = 56, out_features = 1, bias = True)

    def forward(self, states):
        output = nn.Flatten()(states)
        output = self.activation(self.layer1(output))
        output = self.activation(self.layer2(output))
        output = self.layer3(output)

        return output