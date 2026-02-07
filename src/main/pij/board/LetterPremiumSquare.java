package pij.board;

import pij.tile.Tile;

public class LetterPremiumSquare extends Square {

    private int multiplier;

    public LetterPremiumSquare(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    @Override
    public String toString() {
        if (getTile() == null) return String.valueOf(this.multiplier);
        else return getTile().toString();
    }

}
