package pij.board;

/**
 * LetterPremiumSquare is a Square with a special multiplier value.
 */
public class LetterPremiumSquare extends Square {

    public LetterPremiumSquare(int multiplier) {
        setMultiplier(multiplier);
    }

    @Override
    public String toString() {
        if (getTile() == null) return String.valueOf(getMultiplier());
        else return getTile().toString();
    }

}
