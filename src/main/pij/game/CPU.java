package pij.game;

import pij.board.Board;
import pij.board.Coordinate;
import pij.tile.Tile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public final class CPU extends Player {

    public CPU(String name, Board board) {
        super(name, board);
    }

    public Move findFirstMove() {

        /*
        First move has to be in row 7 or column d.
        Works for 7-letter word in hand...
         */
        int handSize = getHand().size();
        var tileSubset = new ArrayList<Tile>();

        // Outer loop: how many letters in the word? Works down from 7 to 2.
        for (int i = handSize; i > 1; i--) {


            // Calculate all permutations of 7 choose 6 at once, add them to list
            var permutations = new ArrayList<List<Tile>>();
            for (int k = 0; k < handSize; k++) {
                var permutation = new ArrayList<Tile>(getHand());
                permutation.remove(k);
                permutations.add(permutation);
            }

            //Not sure what this
//            for (int k = 0; k > handSize - i; k++) {
//                tileSubset.clear();
//                tileSubset.addAll(getHand());
//                if (k > 0) {
//                    tileSubset.remove(k);
//                }
//            }

            Coordinate startSquare = getBoard().getStartSquare();
            int wordLength = i;
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

            //Turn tiles in hand into String
            StringBuilder stringBuilder = new StringBuilder();
            for (var tile : getHand()) {
                stringBuilder.append(tile.getLetter());
            }

            //Somehow have a flag for when there's a wildcard in the hand? This won't happen every turn...

            //Sort word alphabetically
            String sortedWord = alphaSort(stringBuilder.toString());
            Map<String, List<String>> wordsToSorted = new HashMap<>();

            Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");
            try (Stream<String> lines = Files.lines(wordListPath)){
                wordsToSorted = lines.filter(line -> line.length() == wordLength)
                        .map(String::strip)
                        .collect(groupingBy(line -> alphaSort(line)));
            } catch (IOException e) {
                System.out.println("Error finding word list file: " + e.getMessage());
            }

            String targetWord = "";
            if (!wordsToSorted.containsKey(sortedWord)) continue;

            targetWord = wordsToSorted.get(sortedWord).getFirst();

            //Check this CPU has necessary tiles, also remove them.
            var moveInTiles = new ArrayList<Tile>();
            for (char c : targetWord.toCharArray()) {
                Tile tile = new Tile(Character.toUpperCase(c));
                if (getHand().contains(tile)) {
                    moveInTiles.add(tile);
                    getHand().remove(tile);
                } else {
                    tile = new Tile(c);
                    if (getHand().contains(tile)) {
                        moveInTiles.add(tile);
                        getHand().remove(tile);
                    }
                }
            }

            return new Move(moveInTiles, validVertCoordinates.getFirst(), true);

        }
        return null;
    }

    private Move wordSearch(List<Tile> tiles) {
        Coordinate startSquare = getBoard().getStartSquare();
        int wordLength = tiles.size();
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

        //Turn tiles in hand into String
        StringBuilder stringBuilder = new StringBuilder();
        for (var tile : getHand()) {
            stringBuilder.append(tile.getLetter());
        }

        //Somehow have a flag for when there's a wildcard in the hand? This won't happen every turn...

        //Sort word alphabetically
        String sortedWord = alphaSort(stringBuilder.toString());
        Map<String, List<String>> wordsToSorted = new HashMap<>();

        Path wordListPath = Path.of(System.getProperty("user.dir"), "resources", "wordlist.txt");
        try (Stream<String> lines = Files.lines(wordListPath)){
            wordsToSorted = lines.filter(line -> line.length() == wordLength)
                    .map(String::strip)
                    .collect(groupingBy(line -> alphaSort(line)));
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }

        String targetWord = "";
        if (!wordsToSorted.containsKey(sortedWord)) return null;

        targetWord = wordsToSorted.get(sortedWord).getFirst();

        //Check this CPU has necessary tiles, also remove them.
        var moveInTiles = new ArrayList<Tile>();
        for (char c : targetWord.toCharArray()) {
            Tile tile = new Tile(Character.toUpperCase(c));
            if (getHand().contains(tile)) {
                moveInTiles.add(tile);
                getHand().remove(tile);
            } else {
                tile = new Tile(c);
                if (getHand().contains(tile)) {
                    moveInTiles.add(tile);
                    getHand().remove(tile);
                }
            }
        }

        return new Move(moveInTiles, validVertCoordinates.getFirst(), true);


}

    private String alphaSort(String string) {
        var chars = string.toLowerCase().toCharArray();
        Arrays.sort(chars);
        return new String(chars);
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