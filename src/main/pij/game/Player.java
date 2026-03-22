package pij.game;

import pij.board.Board;
import pij.board.LetterPremiumSquare;
import pij.board.WordPremiumSquare;
import pij.exceptions.IllegalMoveException;
import pij.tile.Tile;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

/**
 * Abstract class containing functionality for child classes HumanPlayer and CPU.
 * Tracks tiles in player's hand, player's score, and provides method for making moves on the board.
 */
public abstract sealed class Player permits HumanPlayer, CPU {

    private final String name;
    private final List<Tile> hand = new ArrayList<>();
    private int score = 0;
    private final Board board;

    public Player(String name, Board board) {
        this.name = name;
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public List<Tile> getHand() {
        return this.hand;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public boolean equals(Object other) {
        if (other instanceof Player otherPlayer) {
            return otherPlayer.getName().equals(name);
        }
        return false;
    }

    public void updateScore(int i) {
        this.score += i;
    }

    /**
     * Checks if this player has the required tiles in hand to play a given move.
     * @param word String representation of word to be checked.
     * @throws IllegalMoveException If player does not have necessary tiles in hand.
     */
    public void checkHasTiles(String word) throws IllegalMoveException {

        Map<Character, Integer> letterCount = new HashMap<>();

        for (char c : word.toCharArray()) {
            if (Character.isLowerCase(c)) {
                letterCount.put('_', letterCount.getOrDefault('_',0) + 1);
            } else {
                letterCount.put(c, letterCount.getOrDefault(c,0) + 1);
            }
        }

        Map<Character, Integer> tileCount = getHand().stream()
                .collect(groupingBy(Tile::getLetter, summingInt(tile -> 1)));

        for (Character key : letterCount.keySet()) {
            if (!tileCount.containsKey(key) || letterCount.get(key) > tileCount.get(key)) {
                throw new IllegalMoveException("You don't have the necessary tiles for that word. Please try again:");
            }
        }

    }

    /**
     * Adds given tiles to this player's hand.
     * @param drawnTiles the List of Tile objects to be added.
     */
    public void drawTiles(List<Tile> drawnTiles) {
        hand.addAll(drawnTiles);
    }

    /**
     * Provides a readable String format of tiles in this player's hand.
     * @return String representing this player's hand.
     */
    public String handToString() {
        var sj = new StringJoiner(", ");
        if (!hand.isEmpty()) {
            for (Tile tile : hand) {
                sj.add("[" + tile + "]");
            }
        }
        return sj.toString();
    }

    /**
     * Plays the given move on the board, while updating this player's score. Also returns the score for testing purposes.
     * @param move The move to be played.
     * @return int score of the move, for testing purposes.
     */
    public int makeMove(Move move) {
        if (board.isFirstMove()) board.firstMoveTaken();
        int score = 0;
        int wordMultiplier = 1;
        int x = move.coordinate().x();
        int y = move.coordinate().y();
        int i = 0;

        if (move.vertical()) score += addScoresAbove(x, y);
        else score += addScoresLeft(x, y);

        // Place tiles on the board and add up scores
        while (i < move.word().size()) {
            var square = board.getSquare(x, y);
            if (square.getTile() != null) {
                score += square.getTile().getTileMultiplier();
            }
            else {
                Tile tile = move.word().get(i);
                square.placeTile(tile);
                switch (square) {
                    case WordPremiumSquare wordPremiumSquare -> {
                        wordMultiplier *= wordPremiumSquare.getMultiplier();
                        score += tile.getTileMultiplier();
                    }
                    case LetterPremiumSquare letterPremiumSquare ->
                            score += letterPremiumSquare.getMultiplier() * tile.getTileMultiplier();
                    default -> score += tile.getTileMultiplier();
                }
                i++;
            }

            if (!move.vertical()) x++;
            else y++;
        }

        if (move.vertical()) score += addScoresBelow(x, y);
        else score += addScoresRight(x, y);

        int finalScore = score * wordMultiplier;

        updateScore(finalScore);

        for (Tile tile : move.word()) {
            getHand().remove(tile);
        }

        return finalScore;
    }

    /**
     * When game has ended, deducts the total value of all remaining tiles in this player's hand.
     */
    public void deductRemainingTiles() {
        if (getHand().isEmpty()) return;
        int deduction = 0;
        for (Tile tile : getHand()) {
            deduction += tile.getTileMultiplier();
        }
        updateScore(deduction * -1);
    }

    /**
     * Sums scores of tiles already played on board to the left of given x,y coordinate.
     * @param x x coordinate of start tile.
     * @param y y coordinate of start tile.
     * @return int sum of scores of tiles already on board, 0 if not applicable.
     */
    private int addScoresLeft(int x, int y) {
        x = x - 1;
        int score = 0;
        while (x >= 0 && board.getSquare(x, y).getTile() != null)  {
            score += board.getSquare(x, y).getTile().getTileMultiplier();
            x--;
        }
        return score;
    }

    /**
     * Sums scores of tiles already played on board to the right of given x,y coordinate.
     * @param x x coordinate of start tile.
     * @param y y coordinate of start tile.
     * @return int sum of scores of tiles already on board, 0 if not applicable.
     */
    private int addScoresRight(int x, int y) {
        int score = 0;
        while (x < board.getSizeX() && board.getSquare(x, y).getTile() != null)  {
            score += board.getSquare(x, y).getTile().getTileMultiplier();
            x++;
        }
        return score;
    }

    /**
     * Sums scores of tiles already played on board above given x,y coordinate.
     * @param x x coordinate of start tile.
     * @param y y coordinate of start tile.
     * @return int sum of scores of tiles already on board, 0 if not applicable.
     */
    private int addScoresAbove(int x, int y) {
        y = y - 1;
        int score = 0;
        while (y >= 0 && board.getSquare(x, y).getTile() != null)  {
            score += board.getSquare(x, y).getTile().getTileMultiplier();
            y--;
        }
        return score;
    }

    /**
     * Sums scores of tiles already played on board below given x,y coordinate.
     * @param x x coordinate of start tile.
     * @param y y coordinate of start tile.
     * @return int sum of scores of tiles already on board, 0 if not applicable.
     */
    private int addScoresBelow(int x, int y) {
        int score = 0;
        while (y < board.getSizeY() && board.getSquare(x, y).getTile() != null)  {
            score += board.getSquare(x, y).getTile().getTileMultiplier();
            y++;
        }
        return score;
    }

}
