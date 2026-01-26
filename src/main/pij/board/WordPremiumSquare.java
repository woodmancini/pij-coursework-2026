package pij.board;

public class WordPremiumSquare extends Square {
    int multiplier;
    public WordPremiumSquare(char letter, int multiplier) {
        super(letter);
        this.multiplier = multiplier;
    }
    @Override
    public String toString() {
        return String.valueOf(this.multiplier + "!");
    }
}
