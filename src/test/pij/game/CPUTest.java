package pij.game;

import org.junit.jupiter.api.Test;
import pij.exceptions.IllegalMoveException;
import pij.tile.TileBagHalfSize;

import static org.junit.jupiter.api.Assertions.*;

public class CPUTest {

    private GameRunner runner;

    @Test
    void testEndGameHelloGoodbye() throws IllegalMoveException {
        runner = new TestGameRunner();
        runner.setTileBag(new TileBagHalfSize());
        CPU cpu = new CPU("Player 1", runner.getBoard(), runner);
        runner.setPlayer1(cpu);
        runner.getTileBag().deal(cpu, "RALUDNEP");
        Move move = cpu.findFirstMove();
        String actual = move.wordToString();
        String expected = "PENDULAR";
        assertEquals(expected, actual);
    }

}