package pij.tile;

import pij.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileBag {

    public static final int TILES_PER_PLAYER = 7;
    private final List<Tile> contents = new ArrayList<>();

    public TileBag() {
        addTiles('A', 8);
        addTiles('B', 2);
        addTiles('C', 2);
        addTiles('D', 4);
        addTiles('E', 9);
        addTiles('F', 3);
        addTiles('G', 4);
        addTiles('H', 3);
        addTiles('I', 9);
        addTiles('J', 1);
        addTiles('K', 2);
        addTiles('L', 4);
        addTiles('M', 2);
        addTiles('N', 7);
        addTiles('O', 7);
        addTiles('P', 2);
        addTiles('Q', 1);
        addTiles('R', 6);
        addTiles('S', 4);
        addTiles('T', 5);
        addTiles('U', 5);
        addTiles('V', 2);
        addTiles('W', 2);
        addTiles('X', 1);
        addTiles('Y', 2);
        addTiles('Z', 1);
        addTiles('_', 2);
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

    public void addTiles(char letter, int count) {
        for (int i = 0; i < count; i++) {
            this.contents.add(new Tile(letter));
        }
    }

    public void deal(Player recipient, String word) {
        List<Tile> tiles = new ArrayList<>();
        for (char c : word.toCharArray()) {
            tiles.add(new Tile(c));
        }
        if (tiles.size() > tilesRemaining()) {
            System.out.println("Not enough tiles in the bag.");
            return;
        }
        for (Tile tile : tiles) {
            if (!contents.contains(tile)) {
                System.out.println("Not enough tiles in the bag.");
                return;
            }
            contents.remove(tile);
        }
        recipient.drawTiles(tiles);
    }

    public void deal(Player recipient) {

        if (tilesRemaining() == 0) return;

        int tilesToDeal = TILES_PER_PLAYER - recipient.getHand().size();
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
