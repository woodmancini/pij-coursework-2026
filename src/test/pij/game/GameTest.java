package pij.game;
import org.junit.jupiter.api.Test;
import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;
import pij.tile.TestTileBag;
import pij.tile.Tile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class GameTest {

    private GameRunner runner;


    private static List<Tile> stringToTiles(String input) {
        var result = new ArrayList<Tile>();
        var array = input.toCharArray();
        for (char c : array) {
            result.add(new Tile(c));
        }
        return result;
    }

    int playMove(String word, String position) throws IllegalMoveException {
        Move move = runner.buildMove(word.toCharArray(), position);
        runner.validateMove(move);
        return runner.makeMove(move);
    }

    @Test
    void testEndGameHelloGoodbye() throws IllegalMoveException {
        runner = new GameRunner();
        runner.setBoard(BoardParser.parseBoardFromFile());
        runner.setTileBag(new TestTileBag());
        runner.setPlayer1(new HumanPlayer("Player1"));
        runner.setPlayer2(new HumanPlayer("Player2"));
        runner.Player1.getHand().clear();
        runner.Player1.updateScore(playMove("HELLO","d7"));
        runner.Player2.updateScore(playMove("GODBYE","11b"));
        runner.playGame();
        int actual = runner.endGame();
        int expected = 1;
        assertEquals(expected, actual);
    }
    
}
