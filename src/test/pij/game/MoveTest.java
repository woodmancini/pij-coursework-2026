package pij.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;

public class MoveTest {

    private GameRunner gameRunner;

    @BeforeEach
    void SetUp() {
        gameRunner = new GameRunner();
        gameRunner.setBoard(BoardParser.parseBoardFromFile());
    }

    @Test
    void testMustUseStartSquare() {
        char[] dined ="DINED".toCharArray();
        assertThrows(IllegalMoveException.class, () -> gameRunner.validateMove(gameRunner.buildMove(dined, "d4")));
    }

    @Test
    void test_DINED_d4_Scores20() throws IllegalMoveException {
        char[] dined ="DINED".toCharArray();
        int actual = gameRunner.makeMove(gameRunner.buildMove(dined, "d4"));
        int expected = 20;
        assertEquals(expected, actual);
    }

    @Test
    void testOutOfBoundsMove() {
        char[] aardvarks = "AARDVARKS".toCharArray();
        assertThrows(IllegalMoveException.class, () -> gameRunner.validateMove(gameRunner.buildMove(aardvarks,"d7")));
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

    @Test
    void test_DOVE_4e_Scores9() throws IllegalMoveException {
        char[] dined = "DINED".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(dined, "d4"));
        char[] tenzon = "TNZON".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(tenzon, "7c"));
        char[] dove = "OVE".toCharArray();
        int actual = gameRunner.makeMove(gameRunner.buildMove(dove, "4e"));
        int expected = 9;
        assertEquals(expected, actual);
    }

    @Test
    void test_PERSON_IsValidScores15() throws IllegalMoveException {
        char[] dined = "DINED".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(dined, "d4"));
        char[] tenzon = "TNZON".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(tenzon, "7c"));
        char[] dove = "OVE".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(dove, "4e"));
        char[] person = "PRSN".toCharArray();
        int actual = gameRunner.makeMove(gameRunner.buildMove(person, "g3"));
        int expected = 15;
        assertEquals(expected, actual);
    }

    @Test
    void testParallelMoveIllegal() throws IllegalMoveException {
        char[] dined = "DINED".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(dined, "d4"));
        char[] tenzon = "TNZON".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(tenzon, "7c"));
        char[] dove = "OVE".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(dove, "4e"));
        char[] person = "PRSN".toCharArray();
        gameRunner.makeMove(gameRunner.buildMove(person, "g3"));
        char[] broken = "BRKE".toCharArray();
        assertThrows(IllegalMoveException.class, () -> gameRunner.validateMove(gameRunner.buildMove(broken, "e2")));
    }

}
