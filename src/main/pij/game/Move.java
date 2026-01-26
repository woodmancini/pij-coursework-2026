package pij.game;

import pij.board.Coordinate;
import pij.tile.Tile;
import java.util.List;

public record Move(List<Tile> word, Coordinate coordinate) {}
