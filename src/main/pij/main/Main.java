package pij.main;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;
import pij.game.GameRunner;
import pij.tile.Tile;

public class Main {

    public static void main(String[] args) {

        var gameRunner = new GameRunner();
        gameRunner.startGame();
        gameRunner.playGame();

    }

}
