package pij.board;

public class LetterPremiumSquare extends Square {
    int multiplier;
    public LetterPremiumSquare(char letter, int multiplier) {
        super(letter);
    this.multiplier = multiplier;
    }
    @Override
    public String toString() {
        return String.valueOf(this.multiplier);
    }
}
