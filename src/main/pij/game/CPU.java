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

import static java.util.stream.Collectors.groupingBy;

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

            List<String> playableWords = findPlayableWords(wordLength);

            if (playableWords.isEmpty()) continue;

            var moveInTiles = wordToTiles((playableWords.getFirst()));

            var random = new Random();

//            Randomly play horizontal or vertical move
//            if (random.nextBoolean() && !validVertCoordinates.isEmpty()) return new Move(moveInTiles, validVertCoordinates.getFirst(), true);
//            else if (!validHorizCoordinates.isEmpty()) return new Move(moveInTiles, validHorizCoordinates.getFirst(), false);

            // TO DO Should validate this move before returning it?
            Move move = new Move(moveInTiles, validVertCoordinates.getFirst(), true);
            try {
                runner.validateMove(move);
            } catch (IllegalMoveException e) {
                throw new RuntimeException(e);
            }
            return move;

        }
        return null;
    }

    public Move findMove() {

        getBoard().printPlayable();
        System.out.println(getName() + " is thinking...");
        System.out.println("Played Squares = " + getPlayedSquares());
        var squaresWithTiles = new HashMap<Coordinate, Character>();

        //Can I check the runway at this stage?
        for (int y = 0; y < getBoard().getSizeY(); y++) {
            for (int x = 0; x < getBoard().getSizeX(); x++) {
                var square = getBoard().getSquare(x,y);
                if (square.hasTile()) {
                    squaresWithTiles.put(new Coordinate(x,y), square.getTile().getLetter());
                    //How to retain info that a given square is viableVert/Horiz...
                    //Iterate through squares and return list of Squares which hasTile();
                    //Can also check (later) if square.hasNeighboursX(), square.hasNeighboursY() and set square.playable()
                    //Square knows its own tile/letter so that's fine...
                    //But needs to know its own coordinate...

//                    try {
//                        if (getBoard().getSquare(x + 1, y).hasTile() ||
//                                getBoard().getSquare(x - 1, y).hasTile()) {
//                            viableHoriz = false;
//                        }
//                    } catch (IndexOutOfBoundsException e) {}
//                    try {
//                        if (getBoard().getSquare(key.x(), key.y() + 1).hasTile() ||
//                                getBoard().getSquare(key.x(), key.y() - 1).hasTile()) {
//                            viableVert = false;
//                        }
//                    } catch (IndexOutOfBoundsException e) {}
                }
            }
        }

        System.out.println(squaresWithTiles);

        String handString = handToWord();

        int handSize = getHand().size();

        //With this ordering of the loops, it will try to find an 8-letter word on every tile, then 7, etc
        //Looks for largest possible word, then works its way down
        for (int i = handSize + 1; i > 1; i--) {
            System.out.println(i);
            //Check how long the 'runway' is: do we actually have space to play an 8-letter word?
            //Iterate through squares on the board which already have tiles
            for (var key : squaresWithTiles.keySet()) {
                if (!getBoard().getSquare(key).isPlayable()) continue;
                //if clear on left and right, proceed
                boolean viableHoriz = true;
                boolean viableVert = true;
                try {
                    if (getBoard().getSquare(key.x() + 1, key.y()).hasTile() ||
                            getBoard().getSquare(key.x() - 1, key.y()).hasTile()) {
                        viableHoriz = false;
                    }
                } catch (IndexOutOfBoundsException e) {}
                try {
                    if (getBoard().getSquare(key.x(), key.y() + 1).hasTile() ||
                            getBoard().getSquare(key.x(), key.y() - 1).hasTile()) {
                        viableVert = false;
                    }
                } catch (IndexOutOfBoundsException e) {}

                //Could mark this for the future with a boolean in Tile? if (Tile.playable())...
                //If it's not playable this round, it won't be in future turns either...
                //Tile can't be used because boxed in
                if (!viableVert && !viableHoriz) {
                    getBoard().getSquare(key).notPlayable();
                    continue;
                }

                String handComplete = handString + squaresWithTiles.get(key);
                String handSorted = alphaSort(handComplete);

                if (i == handSize + 1) {
                    var result = alphaSearch(handSorted);
                    if (result.isEmpty()) continue;
                    String wordMatch = result.get();
                    var sb = new StringBuilder(wordMatch);
                    int index = wordMatch.indexOf(Character.toLowerCase(squaresWithTiles.get(key)));
                    sb.deleteCharAt(index);
                    var moveInTiles = wordToTiles(sb.toString());
                    if (viableVert) {
                        if (key.y() >= 0) {
                            Move moveVertical = new Move(moveInTiles, new Coordinate(key.x(), key.y() - index), true);
                            try {
                                runner.validateMove(moveVertical);
                                System.out.println("i is " + i);
                                System.out.println(moveInTiles);
                                System.out.println("USED ALPHA METHOD");
                                return moveVertical;
                            } catch (IllegalMoveException e) {}
                        }
                    }
                    if (viableHoriz) {
                        if (key.x() - index >= 0) {
                            Move moveHorizontal = new Move(moveInTiles, new Coordinate(key.x() - index, key.y()), false);
                            try {
                                runner.validateMove(moveHorizontal);
                                System.out.println("i is " + i);
                                System.out.println(moveInTiles);
                                System.out.println("USED ALPHA METHOD");
                                return moveHorizontal;
                            } catch (IllegalMoveException e) {}
                        }
                    }

                    //alphaSearch for a valid word using max number of tiles in hand
                    //Find index of the key's letter (letter on the board); remove char from word at that index
                    //This index is where to play the move...
                    //Validate move and return
                }

                //alphaSearch 8 choose 7 version of the word because this is easy (fori, sb.deleteCharAt(i), etc)...



                //Check how long the 'runway' is: do we actually have space to play an 8-letter word?
                //All tiles trick - we could use the alphaSort idea to speed things up? And skip the j loop?
                //Try to use tile as first letter in word, then second, etc
                for (int j = 0; j < i; j++) {
                    Optional<String> result = findPlayableWords(i,
                            j, squaresWithTiles.get(key));
                    if (result.isEmpty()) continue;
                    var sb = new StringBuilder(result.get());

                    //Maybe useful for returning the word to be printed to console??
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
                    if (key.y() - j >= 0) {
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


}
