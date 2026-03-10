package pij.main;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;
import pij.game.CPU;
import pij.game.GameRunner;
import pij.game.Move;

public class Main {

    public static void main(String[] args) {

        var runner = new GameRunner();
//        gameRunner.startGame();
//        gameRunner.playGame();
//        gameRunner.endGame();
        var board = BoardParser.parseBoardFromFile();
        runner.setBoard(board);
        var computerPlayer = new CPU("Player 1", board);
        runner.getTileBag().deal(computerPlayer, "JKCATOp");
        Move move = computerPlayer.findFirstMove();
        System.out.println(move.wordToString() + ", " + move.coordinate());
        try {
            runner.validateMove(move);
        } catch (IllegalMoveException e) {
            System.out.println(e.getMessage());
        }
        computerPlayer.makeMove(move);
        board.printBoard();
        System.out.println(computerPlayer.getHand().isEmpty());


    }

}
