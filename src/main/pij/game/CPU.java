package pij.game;

import pij.board.Board;
import pij.board.Coordinate;
import pij.board.Square;
import pij.exceptions.IllegalMoveException;
import pij.tile.Tile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;

/**
 * Computer Player that attempts to play valid words on the board.
 */
public final class CPU extends Player {

    private GameRunner runner;

    public CPU(String name, Board board, GameRunner runner) {
        super(name, board);
        this.runner = runner;
    }

    /**
     * Plays first move on the board, using tiles in this CPU's hand and playing on the board's start square.
     * @return Move to be played.
     */
    public Move findFirstMove() {

        /*
        First move has to be in row 7 or column d.
        Works for 7-letter word in hand...
         */
        int handSize = getHand().size();

        // Outer loop: how many letters in the word? Works down from handSize to 2.
        for (int wordLength = handSize; wordLength > 1; wordLength--) {

            Coordinate startSquare = getBoard().getStartSquare();
            int x = startSquare.x();
            int y = startSquare.y();
            List<Coordinate> validVertCoordinates = new ArrayList<>();
            List<Coordinate> validHorizCoordinates = new ArrayList<>();

            for (int j = wordLength - 1; j >= 0; j--) {
                if (y - j < 0) continue;
                validVertCoordinates.add(new Coordinate(x, y - j));
            }

            for (int j = wordLength - 1; j >= 0; j--) {
                if (x - j < 0) continue;
                validHorizCoordinates.add(new Coordinate(x - j, y));
            }

            Optional<String> playableWord = findPlayableWords(wordLength);

            if (playableWord.isEmpty()) continue;

            var moveInTiles = wordToTiles((playableWord.get()));

            Move move = new Move(moveInTiles, validVertCoordinates.getFirst(), true);
            try {
                runner.validateMove(move);
            } catch (IllegalMoveException e) {continue;}

            System.out.printf("The move is... word: %s at position %s.%n", move.wordToString(), move.getDirectionalCoord());

            return move;

        }
        return null;
    }

    /**
     * Searches board for valid words to play using this CPU's hand. Attempts to play longer words if possible.
     * @return Move to be played on the board.
     */
    public Move findMove() {

        System.out.println(getName() + " is thinking...");

        //Iterate through playedSquares to find those with most space around them
        //(ie squares that are likely to be playable)
        for (var square : getPlayedSquares()) {

            int rightFreeSpace = 0;
            var current = square.getRight();
            while (current != null) {
                if (current.hasTile()) break;
                if (current.getAbove() != null && current.getAbove().hasTile()) break;
                if (current.getBelow() != null && current.getBelow().hasTile()) break;
                rightFreeSpace += 1;
                current = current.getRight();
            }

            int leftFreeSpace = 0;
            current = square.getLeft();
            while (current != null) {
                if (current.hasTile()) break;
                if (current.getAbove() != null && current.getAbove().hasTile()) break;
                if (current.getBelow() != null && current.getBelow().hasTile()) break;
                leftFreeSpace += 1;
                current = current.getLeft();
            }

            int aboveFreeSpace = 0;
            current = square.getAbove();
            while (current != null) {
                if (current.hasTile()) break;
                if (current.getLeft() != null && current.getLeft().hasTile()) break;
                if (current.getRight() != null && current.getRight().hasTile()) break;
                aboveFreeSpace += 1;
                current = current.getAbove();
            }

            int belowFreeSpace = 0;
            current = square.getBelow();
            while (current != null) {
                if (current.hasTile()) break;
                if (current.getLeft() != null && current.getLeft().hasTile()) break;
                if (current.getRight() != null && current.getRight().hasTile()) break;
                belowFreeSpace += 1;
                current = current.getBelow();
            }
            square.setHorizontalSpace(leftFreeSpace + rightFreeSpace);
            square.setVerticalSpace(aboveFreeSpace + belowFreeSpace);
        }

        var prioritySquaresHorizontal = getPlayedSquares().stream()
                .sorted(comparingInt(Square::getHorizontalSpace).reversed())
                .toList();

        var prioritySquaresVertical = getPlayedSquares().stream()
                .sorted(comparingInt(Square::getVerticalSpace).reversed())
                .toList();

        int handSize = getHand().size();

        //Tries to find a max-letter word on every
        //tile with enough space around it, then max - 1, etc
        for (int i = handSize + 1; i > 1; i--) {

            //Iterate over horizontal and vertical priority lists
            for (int x = 0; x < prioritySquaresHorizontal.size(); x++) {
                Square current = prioritySquaresHorizontal.get(x);
                if (current.getHorizontalSpace() >= i) {
                    for (int j = 0; j < i; j++) {
                        Optional<String> result = findPlayableWords(i,
                                j, current.getTile().getLetter());
                        if (result.isEmpty()) continue;
                        var sb = new StringBuilder(result.get());


                        String possibleWord = sb.deleteCharAt(j).toString();
                        var moveInTiles = wordToTiles(possibleWord);
                        if (current.getCoordinate().x() - j >= 0) {
                            Move moveHorizontal = new Move(moveInTiles,
                                    new Coordinate(current.getCoordinate().x() - j, current.getCoordinate().y()),
                                    false);
                            try {
                                runner.validateMove(moveHorizontal);
                                System.out.printf("The move is... word: %s at position %s.%n",
                                        moveHorizontal.wordToString(), moveHorizontal.getDirectionalCoord());
                                return moveHorizontal;
                            } catch (IllegalMoveException e) {
                            }
                        }
                    }
                }
                current = prioritySquaresVertical.get(x);
                if (current.getVerticalSpace() >= i) {
                    for (int j = 0; j < i; j++) {
                        Optional<String> result = findPlayableWords(i,
                                j, current.getTile().getLetter());
                        if (result.isEmpty()) continue;
                        var sb = new StringBuilder(result.get());

                        String possibleWord = sb.deleteCharAt(j).toString();
                        //System.out.println("Possible word to play: " + possibleWord);
                        var moveInTiles = wordToTiles(possibleWord);
                        if (current.getCoordinate().y() - j >= 0) {
                            Move moveVertical = new Move(moveInTiles,
                                    new Coordinate(current.getCoordinate().x(), current.getCoordinate().y() - j),
                                    true);
                            try {
                                runner.validateMove(moveVertical);
                                System.out.printf("The move is... word: %s at position %s.%n",
                                        moveVertical.wordToString(), moveVertical.getDirectionalCoord());
                                return moveVertical;
                            } catch (IllegalMoveException e) {
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes tiles from this CPU's hand and returns them as a List. Only returns as many Tiles
     * as this CPU actually has in hand.
     * @param word String representation of word.
     * @return List of Tiles
     */
    private List<Tile> wordToTiles(String word) {
        var moveInTiles = new ArrayList<Tile>();
        var hand = new ArrayList<>(getHand());
        for (char c : word.toCharArray()) {
            Tile tile = new Tile(Character.toUpperCase(c));
            if (hand.contains(tile)) {
                moveInTiles.add(tile);
                hand.remove(tile);
            } else if (hand.contains(new Tile(c))) {
                tile = new Tile(c);
                if (hand.contains(tile)) {
                    moveInTiles.add(tile);
                    hand.remove(tile);
                }
            }
        }
        return moveInTiles;
    }

    private boolean hasTiles(String targetWord) {
        return hasTiles(targetWord, getHand());
    }

    /**
     * Checks if the passed List has required tiles to play the given word.
     * @param targetWord Word to be played.
     * @param list Tiles that can be played.
     * @return true if successful.
     */
    private boolean hasTiles(String targetWord, List<Tile> list) {
        var hand = new ArrayList<>(list);
        for (char c : targetWord.toCharArray()) {
            Tile tile = new Tile(Character.toUpperCase(c));
            if (hand.contains(tile)) {
                hand.remove(tile);
            } else if (hand.contains(new Tile(c))) {
                tile = new Tile(c);
                if (getHand().contains(tile)) {
                    hand.remove(tile);
                }
            } else return false;
        }
        return true;
    }

    /**
     * Searches wordlist.txt for a playable word of specified length. Also checks if this CPU
     * has required tiles to play word.
     * @param wordLength Length of word to search for.
     * @return Optional containing suitable word if found.
     */
    private Optional<String> findPlayableWords(int wordLength) {

        List<Character> handChars = new ArrayList<>();

        for (var tile : getHand()) {
            handChars.add(Character.toLowerCase(tile.getLetter()));
        }

        Optional<String> playableWord = Optional.empty();
        Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");

        try (Stream<String> lines = Files.lines(wordListPath)) {
            playableWord = lines.map(String::strip)
                    .filter(line -> line.length() == wordLength)
                    //Optimisation - only search words which start with a letter in hand
                    .filter(line -> handChars.contains(line.charAt(0)))
                    .filter(line -> hasTiles(line))
                    .findFirst();
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }
        return playableWord;
    }

    /**
     * Searches wordlist.txt for a playable word given search criteria. Also checks if this CPU
     * has required tiles to play word.
     * @param wordLength Length of word to search for.
     * @param index Index of given letter in the word.
     * @param letterOnBoard Letter that word must contain at given index.
     * @return Optional containing suitable word if found.
     */
    private Optional<String> findPlayableWords(int wordLength, int index, char letterOnBoard) {

        char letterLowerCase = Character.toLowerCase(letterOnBoard);
        List<Character> handChars = new ArrayList<>(letterLowerCase);
        for (var tile : getHand()) {
            handChars.add(Character.toLowerCase(tile.getLetter()));
        }

        List<Tile> completeHand = new ArrayList<>(getHand());
        completeHand.add(new Tile(Character.toUpperCase(letterOnBoard)));

        Optional<String> playableWord = Optional.empty();
        Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");

        try (Stream<String> lines = Files.lines(wordListPath)) {
            playableWord = lines.map(String::strip)
                    .filter(line -> line.length() == wordLength)
                    .filter(line -> line.charAt(index) == letterLowerCase)
                    //Optimisation, only check words that end in a letter in the hand
                    .filter(line -> {
                        if (handChars.contains('_')) return true;
                        return handChars.contains(line.charAt(wordLength - 1));
                    })
                    .filter(line -> hasTiles(line, completeHand))
                    .findFirst();
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }
        return playableWord;
    }

    /**
     * Converts String to lowercase and sorts alphabetically.
     * @param string String to be sorted.
     * @return Lowercase alphabetically sorted version of input String.
     */
    private String alphaSort(String string) {
        var chars = string.toLowerCase().toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    /**
     * Returns first instance of word in wordlist.txt that matches the input when both are alphaSorted, ie they are
     * the same set of letters (including duplicates).
     * @param searchWord The word to be searched.
     * @return Optional containing the matching word (unscrambled).
     */
    private Optional<String> alphaSearch(String searchWord) {

        Optional<String> playableWord = Optional.empty();
        Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");

        try (Stream<String> lines = Files.lines(wordListPath)) {
            playableWord = lines.map(String::strip)
                    .filter(line -> line.length() == searchWord.length())
                    .filter(line -> alphaSort(line).equals(searchWord))
                    .findFirst();
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }

        return playableWord;
    }

    public Optional<String> regexSearch(int wordLength, int index, char letterOnBoard) {

        char letterLowerCase = Character.toLowerCase(letterOnBoard);
        //Remove wildcards because we don't care?
        String tilesSorted = alphaSort(handToWord() + letterOnBoard).replaceAll("_","");

        Optional<String> playableWord = Optional.empty();
        Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");

        try (Stream<String> lines = Files.lines(wordListPath)) {
            playableWord = lines.map(String::strip)
                    .filter(line -> line.length() == wordLength)
                    .filter(line -> line.charAt(index) == letterLowerCase)
                    .filter(line -> {
                        var sj = new StringJoiner("[a-z]*","[a-z]*","[a-z]*");
                        for (char c : alphaSort(line).toCharArray()) {
                            sj.add(String.valueOf(c));
                        }
                        String pattern = sj.toString();
                        return tilesSorted.matches(pattern);
                    })
                    .findFirst();
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }

        return playableWord;
    }
}
