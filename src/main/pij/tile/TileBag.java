package pij.tile;

import pij.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static pij.game.GameRunner.TILES_PER_PLAYER;

public class TileBag {

    private List<Tile> contents = new ArrayList<Tile>();

    public TileBag() {
        contents.add(Tile.J);
        contents.add(Tile.Q);
        contents.add(Tile.X);
        contents.add(Tile.Z);
        for (int i = 0; i < 2; i++) {
            contents.add(Tile.B);
            contents.add(Tile.C);
            contents.add(Tile.K);
            contents.add(Tile.M);
            contents.add(Tile.P);
            contents.add(Tile.V);
            contents.add(Tile.W);
            contents.add(Tile.Y);
            contents.add(Tile.WILDCARD);
        }
        for (int i = 0; i < 3; i++) {
            contents.add(Tile.F);
            contents.add(Tile.H);
        }
        for (int i = 0; i < 4; i++) {
            contents.add(Tile.D);
            contents.add(Tile.G);
            contents.add(Tile.L);
            contents.add(Tile.S);
        }
        for (int i = 0; i < 5; i++) {
            contents.add(Tile.T);
            contents.add(Tile.U);
        }
        for (int i = 0; i < 6; i++) {
            contents.add(Tile.R);
        }
        for (int i = 0; i < 7; i++) {
            contents.add(Tile.N);
            contents.add(Tile.O);
        }
        for (int i = 0; i < 8; i++) {
            contents.add(Tile.A);
        }
        for (int i = 0; i < 9; i++) {
            contents.add(Tile.E);
            contents.add(Tile.I);
        }
    }
    public List<Tile> getContents() {
        return contents;
    }
    public int tilesRemaining() {
        return this.getContents().size();
    }

    public void deal(Player recipient) {

        int tilesToDeal =  TILES_PER_PLAYER - recipient.getHand().size();
        if (tilesToDeal == 0) return;
        List<Tile> returnList = new ArrayList<>();
        var random = new Random();

        //Can't draw more tiles than we have left in contents
        tilesToDeal = Math.min(tilesToDeal, tilesRemaining());

        // remove tiles from bag at random indices and add them to returnList
        for (int i = 0; i < tilesToDeal; i++) {
            int randomIndex = random.nextInt(contents.size());
            returnList.add(contents.remove(randomIndex));
        }

        recipient.drawTiles(returnList);
    }
}
