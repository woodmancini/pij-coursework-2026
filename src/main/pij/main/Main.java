package pij.main;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;
import pij.game.CPU;
import pij.game.GameRunner;
import pij.game.Move;
import pij.game.Player;
import pij.tile.TileBagHalfSize;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        var runner = new GameRunner();
        var board = BoardParser.parseBoardFromFile();
        runner.setBoard(board);
        runner.setTileBag(new TileBagHalfSize());

        var computerPlayer1 = new CPU("Player 1", board, runner);
        runner.setPlayer1(computerPlayer1);
        runner.getTileBag().deal(computerPlayer1);

        var computerPlayer2 = new CPU("Player 2", board, runner);
        runner.setPlayer2(computerPlayer2);
        runner.getTileBag().deal(computerPlayer2);

        runner.playGame();
        runner.endGame();

    }

}
