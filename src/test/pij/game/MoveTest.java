package pij.game;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pij.board.Board;
import pij.board.BoardParser;
import pij.board.Coordinate;
import pij.exceptions.IllegalMoveException;
import pij.tile.Tile;

import java.util.ArrayList;
import java.util.List;


public class MoveTest {

    private GameRunner gameRunner;

    @BeforeEach
    void SetUp() {
        gameRunner = new GameRunner();
        gameRunner.setBoard(BoardParser.parseBoardFromFile());
    }

    @Test
    void testMustUseStartSquare() {
        assertThrows(IllegalMoveException.class, () -> gameRunner.validateMove(new Move(List.of(new Tile('D'), new Tile('I'), new Tile('N'), new Tile('E'), new Tile('D')),
                new Coordinate(3, 7), true)));
    }

    @Test
    void test_DINED_d4_Scores20() {
        int actual = gameRunner.makeMove(new Move(List.of(new Tile('D'), new Tile('I'), new Tile('N'), new Tile('E'), new Tile('D')),
                new Coordinate(3, 3), true));
        int expected = 20;
        assertEquals(expected, actual);
    }

    @Test
    void testOutOfBoundsMove() {

        assertThrows(IllegalMoveException.class, () -> gameRunner.validateMove(new Move(List.of(new Tile('D'), new Tile('I'), new Tile('N'), new Tile('E'), new Tile('D')),
                new Coordinate(3, 7), true)));

    }

    @Test
    void test_TNZON_7c_Scores19() throws IllegalMoveException {
        char[] dined = "DINED".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(dined, "d4"));
        char[] tenzon = "TNZON".toCharArray();
        int actual = gameRunner.makeMove(gameRunner.buildMove(tenzon, "7c"));
        int expected = 19;
        assertEquals(expected, actual);
    }

    //new Move(List.of(new Tile('D'), new Tile('I'), new Tile('N'), new Tile('E'), new Tile('D')


//
//    @Test
//    void testStringToMoveValidVertical() {
//        var gameRunner = new GameRunner();
//        Move actual = gameRunner.stringToMove("DINED,d4");
//        Move expected = new Move(List.of(Tile.D, Tile.I, Tile.N, Tile.E, Tile.D), new Coordinate(3, 3), true);
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testStringToMoveValidHorizontal() {
//        var gameRunner = new GameRunner();
//        Move actual = gameRunner.stringToMove("DINED,4d");
//        Move expected = new Move(List.of(Tile.D, Tile.I, Tile.N, Tile.E, Tile.D), new Coordinate(3, 3), false);
//        assertEquals(expected, actual);
//    }

}
