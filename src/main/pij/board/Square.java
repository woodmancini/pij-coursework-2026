package pij.board;

public class Square {
    char letter;

    public Square(char letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return " " + String.valueOf(this.letter) + " ";
    }
}
