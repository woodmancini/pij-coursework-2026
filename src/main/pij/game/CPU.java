//package pij.game;
//
//import pij.board.Board;
//import pij.board.Coordinate;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public final class CPU extends Player {
//
//    public CPU(String name, Board board) {
//        super(name, board);
//    }
//
//    public Move findFirstMove() {
//
//        /*
//        First move has to be in row 7 or column d.
//        Start with 3-letter word.
//         */
//        Coordinate startSquare = getBoard().getStartSquare();
//        int wordLength = 7;
//        int x = startSquare.x();
//        int y = startSquare.y();
//        List<Coordinate> validCoordinates = new ArrayList<>();
//
//        for (int i = wordLength - 1; i >= 0; i--) {
//            if (y - i < 0) continue;
//            validCoordinates.add(new Coordinate(x,y - i));
//        }
//
//        for (int i = wordLength - 1; i >= 0; i--) {
//            if (x - i < 0) continue;
//            validCoordinates.add(new Coordinate(x - i,y));
//        }
//
//
//        for (var coord : validCoordinates) {
//            System.out.print(coord + " ");
//        }
//
//        StringBuilder stringBuilder = new StringBuilder();
//        for (var tile : getHand()) {
//            stringBuilder.append(tile.getLetter());
//        }
//
//        //sort word alphabetically
//        String sortedWord = alphaSort(stringBuilder.toString());
//        File wordList = new File(System.getProperty("user.dir") + "/resources/wordlist.txt");
//        try {
//            Files.lines(wordList.toPath())
//                    .filter(line -> line.length() == wordLength)
//                    .collect
//                    //Need to check if sorted word equals my word
//        } catch (IOException e){
//            System.out.println("Error finding word list file: " + e.getMessage());
//        }
//        word = word.toLowerCase().strip();
//        try (var reader = new BufferedReader(new FileReader(wordList))) {
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                if (line.strip().equals(word)) return true;
//            }
//        } catch (IOException e) {
//            System.out.println("Error finding word list file: " + e.getMessage());
//        }
//        return false;
//
//
//        //check all three-letter words starting with a letter...
//
//        /*
//        So it has to make A move wherever possible, not necessarily the BEST move.
//        Start with the simplest case: just making a word out of the start square with given tiles.
//        Check... AB, then all words in list starting with AB, then somehow check (containsAll?) if for each word in the
//        list that is < 8 length, I have the necessary tiles to make that word?
//         */
//
//        /*
//        Iterate through matrix, eliminating squares where it's not possible to play a move.
//        For remaining squares, check all permutations of correct amount of tiles, including tile(s) on the board.
//        Make move.
//         */
//
//        /*
//        Hint: A possible strategy to achieve this goal could be the following:
//        1. For each number n of tiles that could be played from the current tile rack (between
//                at most the number of tiles on the rack and at least 1), for each free position on the
//        board, and for both directions, check whether it is possible to play n tiles there.
//        (Often enough, the answer will be false, e.g., because no connection to the existing
//        crossword on the board would be made. There is no need to check all the possible
//        combinations of n tiles for a position and direction where they cannot be played
//        anyway!)
//        2. For those cases of part (1) where it is indeed possible to play n tiles, go through all
//        permutations of n tiles on your rack. If one of them leads to a valid move for the
//        board and the word list, make the move.
//         */
//
//        return null;
//    }
//
//    private String alphaSort(String string) {
//        return new String(string.toCharArray()).toLowerCase();
//    }
//
//}
