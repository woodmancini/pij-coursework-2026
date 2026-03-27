package pij.board;

import java.util.List;

/**
 * Record representing an x/y co-ordinate that refers to a square on the board.
 * @param x x co-ordinate.
 * @param y y co-ordinate.
 */
public record Coordinate(int x, int y) {

    /**
     * Provides String in the format d7.
     * @return String representation of Coordinate.
     */
    @Override
    public String toString(){
        return intToChar(x) + String.valueOf(y + 1);
    }

    private static final List<Character> alphabet = List.of(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    );

    public static char intToChar(int i) {
        return alphabet.get(i);
    }

    public static int charToInt(char c) {
        return alphabet.indexOf(c);
    }

}
