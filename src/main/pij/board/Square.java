package pij.board;

import pij.tile.Tile;

public class Square {

    private Tile tile;

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    @Override
    public String toString() {
        if (this.tile == null) return " _ ";
        else return getTile().toString();
    }

    public void placeTile(Tile tile) {
        setTile(tile);
    }
}
