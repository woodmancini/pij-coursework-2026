package pij.board;

import pij.tile.Tile;

public class Square {

    private int multiplier = 1;

    private Tile tile;

    public Tile getTile() {
        return tile;
    }

    public void placeTile(Tile tile) {
        this.tile = tile;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int m) {
        this.multiplier = m;
    }

    @Override
    public String toString() {
        if (this.tile == null) return " _ ";
        else return getTile().toString();
    }

}
