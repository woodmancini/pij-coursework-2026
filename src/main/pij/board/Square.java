package pij.board;

import pij.tile.Tile;

public class Square {
    private char letter;
    private Tile tile;

    public Square(char letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        if (this.tile == null) return " " + this.letter + " ";
        else return tile.toString();
    }

    public void placeTile(Tile tile) {
        this.tile = tile;
    }
}
