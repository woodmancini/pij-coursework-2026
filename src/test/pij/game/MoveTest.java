package pij.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;
import pij.tile.TestTileBag;

import java.io.File;

public class MoveTest {

    private GameRunner runner;
    Player player1;
    Player player2;

    int playMove(String word, String position, Player player) throws IllegalMoveException {
        Move move = Move.buildMove(word, position);
        runner.validateMove(move);
        return player.makeMove(move);
    }

    @BeforeEach
    void SetUp() {
        runner = new GameRunner();
        var board = BoardParser.parseBoardFromFile();
        player1 = new HumanPlayer("Player 1", board);
        player2 = new HumanPlayer("Player 2", board);
        runner.setBoard(board);
        runner.setPlayer1(player1);
        runner.setPlayer2(player2);
    }

    @Test
    void testHorizontalMoveTopRow() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a1"));
        assertDoesNotThrow(() -> playMove("HELLO","1a", player1));
    }

    @Test
    void testVerticalMoveLeftColumn() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources" + File.separator + "testBoardStart_a1"));
        assertDoesNotThrow(() -> playMove("HELLO","a1", player1));
    }

    @Test
    void testVerticalMoveRightColumn() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        assertDoesNotThrow(() -> playMove("HELLO","p10", player1));
    }

    @Test
    void testHorizontalMoveBottomRow() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        assertDoesNotThrow(() -> playMove("HELLO","14l", player1));
    }

    @Test
    void testVerticalMoveBottomLeft() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a14"));
        assertDoesNotThrow(() -> playMove("HELLO","a10", player1));
    }

    @Test
    void testHorizontalMoveBottomLeft() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a14"));
        assertDoesNotThrow(() -> playMove("HELLO","14a", player1));
    }

    @Test
    void testVerticalMoveTopRight() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p1"));
        assertDoesNotThrow(() -> playMove("HELLO","p1", player1));
    }

    @Test
    void testHorizontalMoveTopRight() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p1"));
        assertDoesNotThrow(() -> playMove("HELLO","1l", player1));
    }

    @Test
    void test_HELLO_1l_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p1"));
        playMove("HELLO","1l", player1);
        int actual = player1.getScore();
        int expected = 2;
        assertEquals(expected, actual);
    }

    @Test
    void test_HELLO_1a_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a1"));
        playMove("HELLO","1a", player1);
        int actual = player1.getScore();
        int expected = -54;
        assertEquals(expected, actual);
    }

    @Test
    void test_HELLO_a1_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_a1"));
        playMove("HELLO","a1", player1);
        int actual = player1.getScore();
        int expected = 17;
        assertEquals(expected, actual);
    }

    @Test
    void test_HELLO_p10_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        playMove("HELLO","p10", player1);
        int actual = player1.getScore();
        int expected = 176;
        assertEquals(expected, actual);
    }

    @Test
    void test_HELLO_14l_Score() throws IllegalMoveException {
        runner.setBoard(BoardParser.parseBoardFromFile("testresources/testBoardStart_p14"));
        playMove("HELLO","14l", player1);
        int actual = player1.getScore();
        int expected = 10;
        assertEquals(expected, actual);
    }

    @Test
    void testMustUseStartSquare() throws IllegalMoveException {
        Move move = Move.buildMove("DINED", "e4");
        assertThrows(IllegalMoveException.class, () -> runner.validateMove(move));
    }

    @Test
    void test_DINED_d4_Scores20() throws IllegalMoveException {
        playMove("DINED","d4", player1);
        int actual = player1.getScore();
        int expected = 20;
        assertEquals(expected, actual);
    }

    @Test
    void testOutOfBoundsMove() throws IllegalMoveException {
        Move move = Move.buildMove("AARDVARKS", "d7");
        assertThrows(IllegalMoveException.class, () -> runner.validateMove(move));
    }

    @Test
    void test_TNZON_7c_Scores19() throws IllegalMoveException {
        playMove("DINED","d4", player1);
        int actual = playMove("TNZON","7c", player2);
        int expected = 19;
        assertEquals(expected, actual);
    }

    @Test
    void test_DOVE_4e_Scores9() throws IllegalMoveException {
        playMove("DINED","d4", player1);
        playMove("TNZON","7c", player2);
        int actual = playMove("OVE","4e", player1);
        int expected = 9;
        assertEquals(expected, actual);
    }

    @Test
    void test_PERSON_Scores15() throws IllegalMoveException {
        playMove("DINED","d4", player1);
        playMove("TNZON","7c", player2);
        playMove("OVE","4e", player1);
        int actual = playMove("PRSN","g3", player2);
        int expected = 15;
        assertEquals(expected, actual);
    }

    @Test
    void testParallelMoveIllegal() throws IllegalMoveException {
        playMove("DINED","d4", player1);
        playMove("TNZON","7c", player2);
        playMove("OVE","4e", player1);
        playMove("PRSN","g3", player1);
        Move illegalMove = Move.buildMove("BRKE", "e2");
        assertThrows(IllegalMoveException.class, () -> runner.validateMove(illegalMove));
    }

    @Test
    void test_DINE_to_DINED() throws IllegalMoveException {
        playMove("DINE","d4", player1);
        playMove("D","d8", player2);
        int actual = player2.getScore();
        int expected = 10;
        assertEquals(expected, actual);
    }

}
