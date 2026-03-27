package pij.board;

/**
 * WordPremiumSquare is a Square with a special multiplier value.
 */
public class WordPremiumSquare extends Square {

    public WordPremiumSquare(int multiplier) {
        setMultiplier(multiplier);
    }

    @Override
    public String toString() {
        if (this.getTile() == null) return getMultiplier() + "!";
        else return getTile().toString();
    }
}
