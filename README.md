this problem, we will look at the task of classifying iris ?owers into sub-species based on their features
using ID3 Decision Trees. We will use four features of the ?owers – the petal width, petal length, sepal
width, and sepal length. It turns out that these four features will be enough to give us a fairly accurate
classi?cation.
Download the ?les hw3train.txt and hw3test.txt from the class website. These are your training and
test sets respectively. The ?les are in ASCII text format, and each line of the ?le contains a feature vector
followed by its label. Each feature vector has four coordinates; the coordinates are separated by spaces.
1. Build an ID3 Decision Tree classi?er based on the data in hw3train.txt. Do not use pruning.
Draw the decision tree that you obtain. For each node of the decision tree, if it is a leaf node, write
down the label that will be predicted for this node, as well as how many of the training data points lie
in this node. If it is an internal node, write down the splitting rule for the node, as well as how many
of the training data points lie in this node.
2. What is the test error of your classi?er in part (1) on the data in hw3test.txt?