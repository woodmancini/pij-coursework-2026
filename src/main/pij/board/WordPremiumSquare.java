package pij.board;

public class WordPremiumSquare extends Square {

    public WordPremiumSquare(int multiplier) {
        this.setMultiplier(multiplier);
    }

    @Override
    public String toString() {
        if (this.getTile() == null) return this.getMultiplier() + "!";
        else return getTile().toString();
    }
}
