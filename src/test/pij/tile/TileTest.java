package pij.tile;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static pij.tile.Tile.toTile;

public class TileTest {

    private static List<Tile> tiles;

    @BeforeAll
    static void setUp() {
        tiles = new TileBag().getContents();
    }

    @Test
    void test100TilesInNewBag() {
        int expected = 100;
        int actual = tiles.size();
        assertEquals(expected, actual);
    }

    @Test
    void testAllTypesOfTilePresent() {
        var tileSet = new HashSet<>(tiles);
        int expected = 27;
        int actual = tileSet.size();
        assertEquals(expected, actual);
    }

    @Test
    void test8ATiles() {
        Map<Tile, Integer> tileCount = new HashMap<>();
        for (Tile tile: tiles) {
            tileCount.put(tile, (tileCount.getOrDefault(tile, 0) + 1));
        }
        int expected = 8;
        int actual = tileCount.get(Tile.A);
        assertEquals(expected, actual);
    }

    @Test
    void testToTileStringWhiteSpace() {
        Tile expected = Tile.T;
        Tile actual = toTile("t ");
        assertEquals(expected, actual);
    }

    @Test
    void testToTileChar() {
        Tile expected = Tile.Z;
        Tile actual = toTile('z');
        assertEquals(expected, actual);
    }

     @Test
    void testToTileThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> toTile('1'));
    }


}
