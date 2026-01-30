package pij.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pij.board.Coordinate;
import pij.tile.Tile;

import java.util.List;


public class MoveTest {

    @Test
    void testAppleMoveIsValidWord() {
        Move move = new Move(List.of(Tile.A, Tile.P, Tile.P, Tile.L, Tile.E), new Coordinate(5, 5));
        assertTrue(move.isValidWord());
    }

}
