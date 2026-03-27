package pij.game;

import pij.board.Coordinate;
import pij.exceptions.IllegalMoveException;
import pij.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public record Move(List<Tile> word, Coordinate coordinate, boolean vertical) {

    /**
     * Provides String representation of word.
     * @return String in format APpLE.
     */
    public String wordToString() {
        var sb = new StringBuilder();
        for (Tile tile : word) {
            sb.append(tile.getLetter());
        }
        return sb.toString();
    }

    /**
     * Provides String representation of coordinate, accounting for vertical/horizontal moves.
     * @return String in the format d7 for vertical move, or 7d for horizontal move.
     */
    public String getDirectionalCoord() {
        String coordString = coordinate().toString();
        if (vertical()) return coordString;
        var sb = new StringBuilder(coordString);
        char alphaIndex = sb.charAt(0);
        sb.deleteCharAt(0);
        sb.append(alphaIndex);
        return sb.toString();
    }

    /**
     * Takes in a String word and String coordinate and returns a new Move object.
     * @param word String representing the word to be played.
     * @param coordinate String in the format d7 or 7d representing the coordinate of start square of the move.
     * @return Move object representing the move.
     * @throws IllegalMoveException for an invalid move (ie coordinate not parsable, index out of bounds for current board).
     */
    public static Move buildMove(String word, String coordinate) throws IllegalMoveException {

        var wordInChar = word.toCharArray();
        List<Tile> wordInTiles = new ArrayList<>();
        boolean vertical = false;
        int x, y, length = coordinate.length();

        for (char c : wordInChar) {
            wordInTiles.add(new Tile(c));
        }

        try {
            if (Character.isLetter(coordinate.charAt(0))) {
                vertical = true;
                x = Coordinate.charToInt(coordinate.charAt(0));
                y = Integer.parseInt(coordinate.substring(1)) - 1;
            } else if (Character.isLetter(coordinate.charAt(length - 1))) {
                x = Coordinate.charToInt(coordinate.charAt(length - 1));
                y = Integer.parseInt(coordinate.substring(0, length - 1)) - 1;
            } else throw new IllegalMoveException(coordinate + " is not a valid square, please try again:");
        } catch (NumberFormatException e) {
            throw new IllegalMoveException(coordinate + " is not a valid square, please try again:");
        }

        return new Move(wordInTiles, new Coordinate(x, y), vertical);

    }


}
