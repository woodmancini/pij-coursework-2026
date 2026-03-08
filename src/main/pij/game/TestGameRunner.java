package pij.game;

import pij.board.Board;
import pij.board.BoardParser;
import pij.tile.TestTileBag;

public class TestGameRunner extends GameRunner {

    public TestGameRunner() {
        Board board = BoardParser.parseBoardFromFile();
        setBoard(board);
        setTileBag(new TestTileBag());
        setPlayer1(new HumanPlayer("Player 1", board));
        setPlayer2(new HumanPlayer("Player 2", board));
    }


}
