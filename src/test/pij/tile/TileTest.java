package pij.tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pij.game.HumanPlayer;
import pij.game.Player;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    private TileBag tileBag;
    private Map<Character, Integer> tileCount;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        tileBag = new TileBag();
        tileCount = new HashMap<>();
        for (Tile tile: tileBag.getContents()) {
            tileCount.put(tile.getLetter(), (tileCount.getOrDefault(tile.getLetter(), 0) + 1));
        }
        testPlayer = new HumanPlayer("testPlayer");
    }

    @Test
    void test100TilesInNewBag() {
        int expected = 100;
        int actual = tileBag.getContents().size();
        assertEquals(expected, actual);
    }

    @Test
    void testAllTypesOfTilePresent() {
        int expected = 27;
        int actual = tileCount.size();
        assertEquals(expected, actual);
    }

    @Test
    void test8ATiles() {
        int expected = 8;
        int actual = tileCount.get('A');
        assertEquals(expected, actual);
    }

    @Test
    void test93TilesAfterDealingHand() {
        tileBag.deal(testPlayer);
        int expected = 93;
        int actual = tileBag.getContents().size();
        assertEquals(expected, actual);
    }

    @Test
    void testPlayerHandContains7Tiles() {
        tileBag.deal(testPlayer);
        int expected = 7;
        int actual = testPlayer.getHand().size();
        assertEquals(expected, actual);
    }

}
