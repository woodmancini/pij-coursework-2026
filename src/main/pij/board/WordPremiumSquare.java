package pij.board;

public class WordPremiumSquare extends Square {

    private int multiplier;

    public WordPremiumSquare(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    @Override
    public String toString() {
        if (this.getTile() == null) return this.multiplier + "!";
        else return getTile().toString();
    }
}
