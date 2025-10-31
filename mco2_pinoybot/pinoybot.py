"""
pinoybot.py

PinoyBot: Filipino Code-Switched Language Identifier

This module provides the main tagging function for the PinoyBot project, which identifies the language of each word in a code-switched Filipino-English text. The function is designed to be called with a list of tokens and returns a list of tags ("ENG", "FIL", or "OTH").

Model training and feature extraction should be implemented in a separate script. The trained model should be saved and loaded here for prediction.
"""

import numpy as np
import os
import pickle
from typing import List
from sklearn.datasets import load_iris
from sklearn.tree import DecisionTreeClassifier, plot_tree #regressor if number
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split

# Main tagging function
def tag_language(tokens: List[str]) -> List[str]:
    """
    Tags each token in the input list with its predicted language.
    Args:
        tokens: List of word tokens (strings).
    Returns:
        tags: List of predicted tags ("ENG", "FIL", or "OTH"), one per token.
    """
    # 1. Load your trained model from disk (e.g., using pickle or joblib)
    #    Example: with open('trained_model.pkl', 'rb') as f: model = pickle.load(f)
    #    (Replace with your actual model loading code)

    # 2. Extract features from the input tokens to create the feature matrix
    #    Example: features = ... (your feature extraction logic here)

    # 3. Use the model to predict the tags for each token
    #    Example: predicted = model.predict(features)

    # 4. Convert the predictions to a list of strings ("ENG", "FIL", or "OTH")
    #    Example: tags = [str(tag) for tag in predicted]

    # 5. Return the list of tags
    #    return tags

    # You can define other functions, import new libraries, or add other Python files as needed, as long as
    # the tag_language function is retained and correctly accomplishes the expected task.

    # Currently, the bot just tags every token as FIL. Replace this with your more intelligent predictions.
    return ['FIL' for i in tokens]

if __name__ == "__main__":
    # Example usage
    iris = load_iris()
    print(np.array(iris['data'][:5]))

    X = np.array(iris['data'])
    y = np.array(iris['target'])
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    model = DecisionTreeClassifier()

    model.fit(X, y)

    plt.figure(figsize=(12,8))
    plot_tree(model, feature_names=iris['feature_names'], class_names=iris['target_names'], filled=True)
    plt.show()

    # example_tokens = ["Love", "kita", "."]
    # print("Tokens:", example_tokens)
    # tags = tag_language(example_tokens)

    predictions = model.predict(X_test)
    print("Predictions:", predictions) 
    print("Actual:", y_test) 