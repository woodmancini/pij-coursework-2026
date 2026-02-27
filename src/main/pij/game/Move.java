package pij.game;

import pij.board.Coordinate;
import pij.tile.Tile;
import java.util.List;

public record Move(List<Tile> word, Coordinate coordinate, boolean vertical) {

    //Not useful?
    public String wordToString() {
        var sb = new StringBuilder();
        for (Tile tile : word) {
            sb.append(tile.getLetter());
        }
        return sb.toString();
    }

}
