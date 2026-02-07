package pij.tile;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class Tile {
    private char letter;
    private final int tileMultiplier;
    private boolean isWildcard;
    private static final Map<Character, Integer> lookUpMultiplier = Map.ofEntries(
            entry('A', 1), entry('B', 3), entry('C', 4), entry('D', 2), entry('E', 2),
            entry('F', 4), entry('G', 3), entry('H', 4), entry('I', 1), entry('J', 11),
            entry('K', 6), entry('L', 1), entry('M', 3), entry('N', 1), entry('O', 1),
            entry('P', 3), entry('Q', 12), entry('R', 1), entry('S', 1), entry('T', 1),
            entry('U', 1), entry('V', 4), entry('W', 4), entry('X', 9), entry('Y', 5),
            entry('Z', 9)
    );

    public Tile(char letter) {
        if (letter == '_' || Character.isLowerCase(letter)) {
            isWildcard = true;
            this.letter = letter;
            this.tileMultiplier = 8;
        } else {
            this.letter = letter;
            this.tileMultiplier = lookUpMultiplier.get(letter);
        }
    }

    public char getLetter() {
        return this.letter;
    }

    public void setLetter(char c) {
        if (this.isWildcard()) this.letter = c;
    }

    public boolean isWildcard(){
        return isWildcard;
    }

    public int getTileMultiplier() {
        return tileMultiplier;
    }

    @Override
    public String toString() {
        return String.valueOf(letter) + tileMultiplier;
    }

    @Override
    public boolean equals(Object object) {
       if (object instanceof Tile other) {
           if (this.isWildcard()) return other.isWildcard();
           else return this.getLetter() == (other.getLetter());
       }
       return false;
    }

}
