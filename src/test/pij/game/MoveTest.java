package pij.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;

import java.io.File;

public class MoveTest {

    private GameRunner runner;

    int playMove(String word, String position) throws IllegalMoveException {
        Move move = runner.buildMove(word.toCharArray(), position);
        runner.validateMove(move);
        return runner.makeMove(move);
    }

    @BeforeEach
    void SetUp() {
        runner = new GameRunner();
        runner.setBoard(BoardParser.parseBoardFromFile());
    }

    @Test
    void testHorizontalMoveTopRow() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a1"));
        assertDoesNotThrow(() -> playMove("HELLO","1a"));
    }

    @Test
    void testVerticalMoveLeftColumn() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources" + File.separator + "testBoardStart_a1"));
        assertDoesNotThrow(() -> playMove("HELLO","a1"));
    }

    @Test
    void testVerticalMoveRightColumn() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        assertDoesNotThrow(() -> playMove("HELLO","p10"));
    }

    @Test
    void testHorizontalMoveBottomRow() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        assertDoesNotThrow(() -> playMove("HELLO","14l"));
    }

    @Test
    void test_HELLO_1a_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a1"));
        int actual = playMove("HELLO","1a");
        int expected = -54;
        assertEquals(expected, actual);
    }

    @Test
    void test_HELLO_a1_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a1"));
        int actual = playMove("HELLO","a1");
        int expected = 17;
        assertEquals(expected, actual);
    }

    @Test
    void test_HELLO_p10_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        int actual = playMove("HELLO","p10");
        int expected = 176;
        assertEquals(expected, actual);
    }

    @Test
    void test_HELLO_14l_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        int actual = playMove("HELLO","14l");
        int expected = 10;
        assertEquals(expected, actual);
    }

    @Test
    void testMustUseStartSquare() throws IllegalMoveException {
        Move move = runner.buildMove("DINED".toCharArray(), "e4");
        assertThrows(IllegalMoveException.class, () -> runner.validateMove(move));
    }

    @Test
    void test_DINED_d4_Scores20() throws IllegalMoveException {
        int actual = playMove("DINED","d4");
        int expected = 20;
        assertEquals(expected, actual);
    }

    @Test
    void testOutOfBoundsMove() throws IllegalMoveException {
        Move move = runner.buildMove("AARDVARKS".toCharArray(), "d7");
        assertThrows(IllegalMoveException.class, () -> runner.validateMove(move));
    }

    @Test
    void test_TNZON_7c_Scores19() throws IllegalMoveException {
        playMove("DINED","d4");
        int actual = playMove("TNZON","7c");
        int expected = 19;
        assertEquals(expected, actual);
    }

    @Test
    void test_DOVE_4e_Scores9() throws IllegalMoveException {
        playMove("DINED","d4");
        playMove("TNZON","7c");
        int actual = playMove("OVE","4e");
        int expected = 9;
        assertEquals(expected, actual);
    }

    @Test
    void test_PERSON_Scores15() throws IllegalMoveException {
        playMove("DINED","d4");
        playMove("TNZON","7c");
        playMove("OVE","4e");
        int actual = playMove("PRSN","g3");
        int expected = 15;
        assertEquals(expected, actual);
    }

    @Test
    void testParallelMoveIllegal() throws IllegalMoveException {
        playMove("DINED","d4");
        playMove("TNZON","7c");
        playMove("OVE","4e");
        playMove("PRSN","g3");
        Move illegalMove = runner.buildMove("BRKE".toCharArray(), "e2");
        assertThrows(IllegalMoveException.class, () -> runner.validateMove(illegalMove));
    }

    @Test
    void test_DINE_to_DINED() throws IllegalMoveException {
        playMove("DINE","d4");
        int actual = playMove("D","d8");
        int expected = 10;
        assertEquals(expected, actual);
    }

}
