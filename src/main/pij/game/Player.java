package pij.game;

import pij.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public abstract sealed class Player permits HumanPlayer, CPU {
    private String name;
    private List<Tile> hand = new ArrayList<>();
    private int score = 0;
    public List<Tile> getHand() {
        return this.hand;
    }
    public void drawTiles(List<Tile> drawnTiles) {
        hand.addAll(drawnTiles);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Player(String name) {
        this.name = name;
    }

    public String printHand() {
        var sj = new StringJoiner(", ");
        if (!hand.isEmpty()) {
            for (Tile tile : hand) {
                sj.add("[" + tile + "]");
            }
        }
        return sj.toString();
    }

    public void updateScore(int i) {
        this.score += i;
    }
}
