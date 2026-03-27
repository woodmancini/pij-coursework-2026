package pij.board;

import pij.tile.Tile;

public class Square {

    private int horizontalSpace;

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    private int verticalSpace;
    public Square getAbove() {
        return above;
    }

    public void setAbove(Square above) {
        this.above = above;
    }

    public Square getBelow() {
        return below;
    }

    public void setBelow(Square below) {
        this.below = below;
    }

    public Square getLeft() {
        return left;
    }

    public void setLeft(Square left) {
        this.left = left;
    }

    public Square getRight() {
        return right;
    }

    public void setRight(Square right) {
        this.right = right;
    }

    private Square above;
    private Square below;
    private Square left;
    private Square right;

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
