package pij.game;

import org.junit.jupiter.api.Test;
import pij.exceptions.IllegalMoveException;
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

    void playMove(String word, String position, Player player) throws IllegalMoveException {
        Move move = Move.buildMove(word.toCharArray(), position);
        runner.validateMove(move);
        player.makeMove(move);
    }

    @Test
    void testEndGameHelloGoodbye() throws IllegalMoveException {
        runner = new TestGameRunner();
        runner.getTileBag().deal(runner.getPlayer1(), "HELLOND");
        runner.getTileBag().deal(runner.getPlayer2(), "GODBYE");
        playMove("HELLO", "d7", runner.getPlayer1());
        playMove("GODBYE", "11c", runner.getPlayer2());
        runner.playGame();
        int actual = runner.endGame();
        int expected = 1;
        assertEquals(expected, actual);
    }
    
}
