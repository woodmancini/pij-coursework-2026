package pij.tile;

import pij.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileBag {

    public static final int TILES_PER_PLAYER = 7;
    private final List<Tile> contents = new ArrayList<>();

    public TileBag() {
        addTiles(contents, 'A', 8);
        addTiles(contents, 'B', 2);
        addTiles(contents, 'C', 2);
        addTiles(contents, 'D', 4);
        addTiles(contents, 'E', 9);
        addTiles(contents, 'F', 3);
        addTiles(contents, 'G', 4);
        addTiles(contents, 'H', 3);
        addTiles(contents, 'I', 9);
        addTiles(contents, 'J', 1);
        addTiles(contents, 'K', 2);
        addTiles(contents, 'L', 4);
        addTiles(contents, 'M', 2);
        addTiles(contents, 'N', 7);
        addTiles(contents, 'O', 7);
        addTiles(contents, 'P', 2);
        addTiles(contents, 'Q', 1);
        addTiles(contents, 'R', 6);
        addTiles(contents, 'S', 4);
        addTiles(contents, 'T', 5);
        addTiles(contents, 'U', 5);
        addTiles(contents, 'V', 2);
        addTiles(contents, 'W', 2);
        addTiles(contents, 'X', 1);
        addTiles(contents, 'Y', 2);
        addTiles(contents, 'Z', 1);
        addTiles(contents, '_', 2);
    }

    public List<Tile> getContents() {
        return contents;
    }

    // Only for testing
    public void emptyBag() {
        this.contents.clear();
    }

    public int tilesRemaining() {
        return this.getContents().size();
    }

    private void addTiles(List<Tile> contents, char letter, int count) {
        for (int i = 0; i < count; i++) {
            contents.add(new Tile(letter));
        }
    }

    public void deal(Player recipient) {

        int tilesToDeal =  TILES_PER_PLAYER - recipient.getHand().size();
        if (tilesToDeal == 0) return;
        List<Tile> returnList = new ArrayList<>();
        var random = new Random();

        // Can't draw more tiles than we have left in contents
        tilesToDeal = Math.min(tilesToDeal, tilesRemaining());

        // Remove tiles from bag at random indices and add them to returnList
        for (int i = 0; i < tilesToDeal; i++) {
            int randomIndex = random.nextInt(contents.size());
            returnList.add(contents.remove(randomIndex));
        }

        recipient.drawTiles(returnList);
    }
}
