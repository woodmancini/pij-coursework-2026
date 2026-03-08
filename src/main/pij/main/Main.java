package pij.main;

import pij.game.GameRunner;

public class Main {

    static void main(String[] args) {

        var gameRunner = new GameRunner();
        gameRunner.startGame();
        gameRunner.playGame();
        gameRunner.endGame();

    }

}
