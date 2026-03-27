package pij.main;

import pij.game.GameRunner;


public class Main {

    public static void main(String[] args) {

        var runner = new GameRunner();

        runner.startGame();
        runner.playGame();
        runner.endGame();

    }

}
