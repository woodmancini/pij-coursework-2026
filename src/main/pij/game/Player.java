package pij.game;

import pij.tile.Tile;
import java.util.List;

public abstract sealed class Player permits HumanPlayer, CPU {

    private int score;
    private List<Tile> hand;

}
