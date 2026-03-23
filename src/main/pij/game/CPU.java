package pij.game;

import pij.board.Board;
import pij.board.Coordinate;
import pij.exceptions.IllegalMoveException;
import pij.tile.Tile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class CPU extends Player {

    private GameRunner runner;

    public CPU(String name, Board board, GameRunner runner) {
        super(name, board);
        this.runner = runner;
    }

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

            for (var coord : validVertCoordinates) {
                System.out.print("Valid starting coordinates for vertical move: " + coord + " ");
                System.out.println();
            }

            for (var coord : validHorizCoordinates) {
                System.out.print("Valid starting coordinates for horizontal move: " + coord + " ");
                System.out.println();
            }

            List<Character> handChars = new ArrayList<>();
            for (var tile : getHand()) {
                handChars.add(Character.toLowerCase(tile.getLetter()));
            }

            List<String> playableWords = findPlayableWords(wordLength);

            System.out.println(playableWords);

            if (playableWords.isEmpty()) continue;

            var moveInTiles = wordToTiles((playableWords.getFirst()));

            var random = new Random();

//            Randomly play horizontal or vertical move
//            if (random.nextBoolean() && !validVertCoordinates.isEmpty()) return new Move(moveInTiles, validVertCoordinates.getFirst(), true);
//            else if (!validHorizCoordinates.isEmpty()) return new Move(moveInTiles, validHorizCoordinates.getFirst(), false);
            return new Move(moveInTiles, validVertCoordinates.getFirst(), true);

        }
        return null;
    }

    public Move findMove() {

        System.out.println(getName() + " is thinking...");
        var squaresWithTiles = new HashMap<Coordinate, Character>();

        for (int y = 0; y < getBoard().getSizeY(); y++) {
            for (int x = 0; x < getBoard().getSizeX(); x++) {
                var square = getBoard().getSquare(x,y);
                if (square.hasTile()) {
                    squaresWithTiles.put(new Coordinate(x,y), square.getTile().getLetter());
                }
            }
        }

        //Looks for largest possible word, then works its way down
        for (int i = getHand().size() + 1; i > 1; i--) {
            System.out.println(i);
            //Check how long the 'runway' is: do we actually have space to play an 8-letter word?
            //Iterate through squares on the board which already have tiles
            for (var key : squaresWithTiles.keySet()) {
                //All tiles trick - we could use the alphaSort idea to speed things up? And skip the j loop?
                //Try to use tile as first letter in word, then second, etc
                for (int j = 0; j < i; j++) {
                    Optional<String> result = findPlayableWords(i,
                            j, squaresWithTiles.get(key));
                    if (result.isEmpty()) continue;
                    var sb = new StringBuilder(result.get());
                    //String entireWord = sb.toString();
                    String possibleWord = sb.deleteCharAt(j).toString();
                    //System.out.println("Possible word to play: " + possibleWord);
                    var moveInTiles = wordToTiles(possibleWord);
                    if (key.x() - j >= 0) {
                        Move moveHorizontal = new Move(moveInTiles, new Coordinate(key.x() - j, key.y()), false);
                        try {
                            runner.validateMove(moveHorizontal);
                            System.out.println("j is " + j);
                            System.out.println("i is " + i);
                            System.out.println(moveInTiles);
                            return moveHorizontal;
                        } catch (IllegalMoveException e) {}
                    }
                    if (key.y() >= 0) {
                        Move moveVertical = new Move(moveInTiles, new Coordinate(key.x(), key.y() - j), true);
                        try {
                            runner.validateMove(moveVertical);
                            System.out.println("j is " + j);
                            System.out.println("i is " + i);
                            System.out.println(moveInTiles);
                            return moveVertical;
                        } catch (IllegalMoveException e) {}
                    }
                }
            }
        }
        return null;
    }

    //So this only returns Tiles that the player actually has...
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

    private List<String> findPlayableWords(int wordLength) {

        List<Character> handChars = new ArrayList<>();

        for (var tile : getHand()) {
            handChars.add(Character.toLowerCase(tile.getLetter()));
        }

        List<String> playableWords = new ArrayList<String>();
        Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");

        try (Stream<String> lines = Files.lines(wordListPath)) {
            playableWords = lines.map(String::strip)
                    .filter(line -> line.length() == wordLength)
                    //Optimisation - only search words which start with a letter in hand
                    .filter(line -> handChars.contains(line.charAt(0)))
                    .filter(line -> hasTiles(line))
                    .toList();
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }
        return playableWords;
    }

    private Optional<String> findPlayableWords(int wordLength, int index, char letterOnBoard) {

        char letterLowerCase = Character.toLowerCase(letterOnBoard);

        List<Tile> completeHand = new ArrayList<>(getHand());
        completeHand.add(new Tile(Character.toUpperCase(letterOnBoard)));

        Optional<String> playableWord = Optional.empty();
        Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");

        try (Stream<String> lines = Files.lines(wordListPath)) {
            playableWord = lines.map(String::strip)
                    .filter(line -> line.length() == wordLength)
                    .filter(line -> line.charAt(index) == letterLowerCase)
                    .filter(line -> hasTiles(line, completeHand))
                    .findFirst();
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }
        return playableWord;
    }


}


/*
        So it has to make A move wherever possible, not necessarily the BEST move.
        Start with the simplest case: just making a word out of the start square with given tiles.
        Check... AB, then all words in list starting with AB, then somehow check (containsAll?) if for each word in the
        list that is < 8 length, I have the necessary tiles to make that word?
         */

        /*
        Iterate through matrix, eliminating squares where it's not possible to play a move.
        For remaining squares, check all permutations of correct amount of tiles, including tile(s) on the board.
        Make move.
         */

        /*
        Hint: A possible strategy to achieve this goal could be the following:
        1. For each number n of tiles that could be played from the current tile rack (between
                at most the number of tiles on the rack and at least 1), for each free position on the
        board, and for both directions, check whether it is possible to play n tiles there.
        (Often enough, the answer will be false, e.g., because no connection to the existing
        crossword on the board would be made. There is no need to check all the possible
        combinations of n tiles for a position and direction where they cannot be played
        anyway!)
        2. For those cases of part (1) where it is indeed possible to play n tiles, go through all
        permutations of n tiles on your rack. If one of them leads to a valid move for the
        board and the word list, make the move.
         */