package pij.main;

import pij.board.BoardParser;
import pij.game.CPU;
import pij.game.GameRunner;

public class Main {

    static void main(String[] args) {

//        var gameRunner = new GameRunner();
//        gameRunner.startGame();
//        gameRunner.playGame();
//        gameRunner.endGame();
        var board = BoardParser.parseBoardFromFile();
        var computerPlayer = new CPU("Player 1", board);
        computerPlayer.findFirstMove();

    }

}
