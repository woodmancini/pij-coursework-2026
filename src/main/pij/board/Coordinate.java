package pij.board;

import java.util.List;

public record Coordinate(int x, int y) {

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

    //Could add coordinate.left() and coordinate.right() to return new coordinate in that direction?

}
