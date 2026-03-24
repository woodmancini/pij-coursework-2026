package pij.main;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;
import pij.game.CPU;
import pij.game.GameRunner;
import pij.game.Move;
import pij.tile.TileBagHalfSize;

public class Main {

    public static void main(String[] args) throws IllegalMoveException {

        var runner = new GameRunner();
        var board = BoardParser.parseBoardFromFile();
        runner.setBoard(board);
        runner.setTileBag(new TileBagHalfSize());

        var computerPlayer1 = new CPU("Player 1", board, runner);
        runner.setPlayer1(computerPlayer1);
        runner.getTileBag().deal(computerPlayer1, "ENDULAR");

        var computerPlayer2 = new CPU("Player 2", board, runner);
        runner.setPlayer2(computerPlayer2);
        runner.getTileBag().deal(computerPlayer2, "PPLEASE");

        computerPlayer2.makeMove(Move.buildMove("P", "d7"));

        runner.playGame();
        runner.endGame();

    }

}
