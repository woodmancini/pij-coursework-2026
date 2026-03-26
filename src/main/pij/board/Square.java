package pij.board;

import pij.tile.Tile;

public class Square {

    private boolean playable = true;

    private int multiplier = 1;

    private Tile tile;

    private Coordinate coordinate;

    public Tile getTile() {
        return tile;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coord) {
        this.coordinate = coord;
    }

    public void setCoordinate(int x, int y) {
        this.coordinate = new Coordinate(x, y);
    }

    public void placeTile(Tile tile) {
        this.tile = tile;
    }

    public boolean hasTile() {
        return tile != null;
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

    public void notPlayable() {
        this.playable = false;
    }

    public boolean isPlayable() {
        return playable;
    }

}
