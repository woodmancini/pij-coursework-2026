package pij.game;

import pij.board.BoardParser;
import pij.tile.TestTileBag;
import pij.tile.Tile;

import java.util.List;

public class TestGameRunner extends GameRunner {

    public TestGameRunner() {
        setBoard(BoardParser.parseBoardFromFile());
        setTileBag(new TestTileBag());
        setPlayer1(new HumanPlayer("Player 1"));
        setPlayer2(new HumanPlayer("Player 2"));
        tileBag.deal(Player1, List.of(new Tile('H'), new Tile('E'), new Tile('L'),
                new Tile('L'), new Tile('O'), new Tile('N'), new Tile('D')));

    }


}
