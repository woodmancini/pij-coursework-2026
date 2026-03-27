package pij.game;

import org.junit.jupiter.api.Test;
import pij.exceptions.IllegalMoveException;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private GameRunner runner;

    void playMove(String word, String position, Player player) throws IllegalMoveException {
        Move move = Move.buildMove(word, position);
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
