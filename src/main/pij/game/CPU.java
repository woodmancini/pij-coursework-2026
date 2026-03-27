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

    public Move findMoveBetter() {

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

//    public Move findMove() {
//
//        System.out.println(getName() + " is thinking...");
//        var squaresWithTiles = new HashMap<Coordinate, Character>();
//
//        //Can I check the runway at this stage?
//        for (int y = 0; y < getBoard().getSizeY(); y++) {
//            for (int x = 0; x < getBoard().getSizeX(); x++) {
//                var square = getBoard().getSquare(x,y);
//                if (square.hasTile()) {
//                    squaresWithTiles.put(new Coordinate(x,y), square.getTile().getLetter());
//                    //How to retain info that a given square is viableVert/Horiz...
//                    //Iterate through squares and return list of Squares which hasTile();
//                    //Can also check (later) if square.hasNeighboursX(), square.hasNeighboursY() and set square.playable()
//                    //Square knows its own tile/letter so that's fine...
//                    //But needs to know its own coordinate...
//
////                    try {
////                        if (getBoard().getSquare(x + 1, y).hasTile() ||
////                                getBoard().getSquare(x - 1, y).hasTile()) {
////                            viableHoriz = false;
////                        }
////                    } catch (IndexOutOfBoundsException e) {}
////                    try {
////                        if (getBoard().getSquare(key.x(), key.y() + 1).hasTile() ||
////                                getBoard().getSquare(key.x(), key.y() - 1).hasTile()) {
////                            viableVert = false;
////                        }
////                    } catch (IndexOutOfBoundsException e) {}
//                }
//            }
//        }
//
//        Map<Square,List<Integer>> squaresToFreeSpace = new HashMap<>();
//        Map<Square,Integer> priorityMapHorizontal = new HashMap<>();
//        Map<Square,Integer> priorityMapVertical = new HashMap<>();
//
//        for (var square : getPlayedSquares()) {
//
//
//
//            int rightFreeSpace = 0;
//            var current = square.getRight();
//            while (current != null) {
//                if (current.hasTile()) break;
//                if (current.getAbove() != null && current.getAbove().hasTile()) break;
//                if (current.getBelow() != null && current.getBelow().hasTile()) break;
//                rightFreeSpace += 1;
//                current = current.getRight();
//            }
//
//            int leftFreeSpace = 0;
//            current = square.getLeft();
//            while (current != null) {
//                if (current.hasTile()) break;
//                if (current.getAbove() != null && current.getAbove().hasTile()) break;
//                if (current.getBelow() != null && current.getBelow().hasTile()) break;
//                leftFreeSpace += 1;
//                current = current.getLeft();
//            }
//
//            int aboveFreeSpace = 0;
//            current = square.getAbove();
//            while (current != null) {
//                if (current.hasTile()) break;
//                if (current.getLeft() != null && current.getLeft().hasTile()) break;
//                if (current.getRight() != null && current.getRight().hasTile()) break;
//                aboveFreeSpace += 1;
//                current = current.getAbove();
//            }
//
//            int belowFreeSpace = 0;
//            current = square.getBelow();
//            while (current != null) {
//                if (current.hasTile()) break;
//                if (current.getLeft() != null && current.getLeft().hasTile()) break;
//                if (current.getRight() != null && current.getRight().hasTile()) break;
//                belowFreeSpace += 1;
//                current = current.getBelow();
//            }
//            squaresToFreeSpace.put(square, List.of(leftFreeSpace, rightFreeSpace, aboveFreeSpace, belowFreeSpace));
//            square.setHorizontalSpace(leftFreeSpace + rightFreeSpace);
//            square.setVerticalSpace(aboveFreeSpace + belowFreeSpace);
//            priorityMapHorizontal.put(square, leftFreeSpace + rightFreeSpace);
//            priorityMapVertical.put(square, aboveFreeSpace + belowFreeSpace);
//        }
//        System.out.println(squaresToFreeSpace);
//
//        var prioritySquaresHorizontal = getPlayedSquares().stream()
//                .sorted(comparingInt(Square::getHorizontalSpace).reversed())
//                        .toList();
//
//        var prioritySquaresVertical = getPlayedSquares().stream()
//                .sorted(comparingInt(Square::getVerticalSpace).reversed())
//                        .toList();
//
////        List<Square> prioritySquaresHorizontal = squaresToFreeSpace.entrySet().stream()
////                .sorted((entry1, entry2) -> {
////                    int space1 = entry1.getValue().get(0) + entry1.getValue().get(1);
////                    int space2 = entry2.getValue().get(0) + entry2.getValue().get(1);
////                    return Integer.compare(space2, space1);
////                })
////                .map(Map.Entry::getKey)
////                .toList();
//
//        System.out.println("Horizontal priority" + prioritySquaresHorizontal);
//
////        List<Square> prioritySquaresVertical = squaresToFreeSpace.entrySet().stream()
////                .sorted((entry1, entry2) -> {
////                    int space1 = entry1.getValue().get(2) + entry1.getValue().get(3);
////                    int space2 = entry2.getValue().get(2) + entry2.getValue().get(3);
////                    return Integer.compare(space2, space1);
////                })
////                .map(Map.Entry::getKey)
////                .toList();
//
//        System.out.println("Vert priority"+prioritySquaresVertical);
//
//
//
//
////        Stream<Square> stream = squaresToFreeSpace.keySet().stream()
////                .filter()
//
//        String handString = handToWord();
//        String entireWord = "";
//        int handSize = getHand().size();
//
//        //With this ordering of the loops, it will try to find an 8-letter word on every tile, then 7, etc
//        //Looks for largest possible word, then works its way down
//        for (int i = handSize + 1; i > 1; i--) {
//            System.out.println(i);
//
//            for (int x = 0; x < prioritySquaresHorizontal.size(); x++) {
//                Square current = prioritySquaresHorizontal.get(x);
//                if (current.getHorizontalSpace() > i) {
//                    //find move logic: wordLength = i
//                    for (int j = 0; j < i; j++) {
//                        Optional<String> result = regexSearch(i,
//                                j, current.getTile().getLetter());
//                        if (result.isEmpty()) continue;
//                        var sb = new StringBuilder(result.get());
//
//                        //Maybe useful for returning the word to be printed to console??
//                        entireWord = sb.toString();
//
//                        String possibleWord = sb.deleteCharAt(j).toString();
//                        //System.out.println("Possible word to play: " + possibleWord);
//                        var moveInTiles = wordToTiles(possibleWord);
//                        if (current.getCoordinate().x() - j >= 0) {
//                            Move moveHorizontal = new Move(moveInTiles,
//                                    new Coordinate(current.getCoordinate().x() - j, current.getCoordinate().y()),
//                                    false);
//                            try {
//                                runner.validateMove(moveHorizontal);
//                                System.out.println("j is " + j);
//                                System.out.println("i is " + i);
//                                System.out.println(moveInTiles);
//                                return moveHorizontal;
//                            } catch (IllegalMoveException e) {}
//                        }
//                    }
//                }
//                current = prioritySquaresVertical.get(x);
//                if (current.getVerticalSpace() > i) {
//                    //find vertical move
//                    for (int j = 0; j < i; j++) {
//                        Optional<String> result = regexSearch(i,
//                                j, current.getTile().getLetter());
//                        if (result.isEmpty()) continue;
//                        var sb = new StringBuilder(result.get());
//
//                        //Maybe useful for returning the word to be printed to console??
//                        entireWord = sb.toString();
//
//                        String possibleWord = sb.deleteCharAt(j).toString();
//                        //System.out.println("Possible word to play: " + possibleWord);
//                        var moveInTiles = wordToTiles(possibleWord);
//                        if (current.getCoordinate().y() - j >= 0) {
//                            Move moveVertical = new Move(moveInTiles,
//                                    new Coordinate(current.getCoordinate().x(), current.getCoordinate().y() - j),
//                                    true);
//                            try {
//                                runner.validateMove(moveVertical);
//                                System.out.println("j is " + j);
//                                System.out.println("i is " + i);
//                                System.out.println(moveInTiles);
//                                return moveVertical;
//                            } catch (IllegalMoveException e) {}
//                        }
//                    }
//                }
//                for (int j = 0; j < i; j++) {
//                    Optional<String> result = regexSearch(i,
//                            j, current.getTile().getLetter());
//                    if (result.isEmpty()) continue;
//                    var sb = new StringBuilder(result.get());
//
//                    //Maybe useful for returning the word to be printed to console??
//                    String entireWord = sb.toString();
//
//                    String possibleWord = sb.deleteCharAt(j).toString();
//                    //System.out.println("Possible word to play: " + possibleWord);
//                    var moveInTiles = wordToTiles(possibleWord);
//                    if (current.getCoordinate().x() - j >= 0) {
//                        Move moveHorizontal = new Move(moveInTiles, new Coordinate(current.getCoordinate().x() - j, current.getCoordinate().y()), false);
//                        try {
//                            runner.validateMove(moveHorizontal);
//                            System.out.println("j is " + j);
//                            System.out.println("i is " + i);
//                            System.out.println(moveInTiles);
//                            return moveHorizontal;
//                        } catch (IllegalMoveException e) {}
//                    }
//                    if (key.y() - j >= 0) {
//                        Move moveVertical = new Move(moveInTiles, new Coordinate(key.x(), key.y() - j), true);
//                        try {
//                            runner.validateMove(moveVertical);
//                            System.out.println("j is " + j);
//                            System.out.println("i is " + i);
//                            System.out.println(moveInTiles);
//                            return moveVertical;
//                        } catch (IllegalMoveException e) {}
//                    }
//                }
//            }
//
//
//            //Check how long the 'runway' is: do we actually have space to play an 8-letter word?
//            //Iterate through squares on the board which already have tiles
//            for (var key : squaresWithTiles.keySet()) {
//                if (!getBoard().getSquare(key).isPlayable()) continue;
////                if clear on left and right, proceed
//                boolean viableHoriz = true;
//                boolean viableVert = true;
//                try {
//                    if (getBoard().getSquare(key.x() + 1, key.y()).hasTile() ||
//                            getBoard().getSquare(key.x() - 1, key.y()).hasTile()) {
//                        viableHoriz = false;
//                    }
//                } catch (IndexOutOfBoundsException e) {}
//                try {
//                    if (getBoard().getSquare(key.x(), key.y() + 1).hasTile() ||
//                            getBoard().getSquare(key.x(), key.y() - 1).hasTile()) {
//                        viableVert = false;
//                    }
//                } catch (IndexOutOfBoundsException e) {}
//
////                Could mark this for the future with a boolean in Tile? if (Tile.playable())...
////                If it's not playable this round, it won't be in future turns either...
////                Tile can't be used because boxed in
//                if (!viableVert && !viableHoriz) {
//                    getBoard().getSquare(key).notPlayable();
//                    continue;
//                }
//
//                String handComplete = handString + squaresWithTiles.get(key);
//                String handSorted = alphaSort(handComplete);
//
//                if (i == handSize + 1) {
//                    var result = alphaSearch(handSorted);
//                    if (result.isEmpty()) continue;
//                    String wordMatch = result.get();
//                    var sb = new StringBuilder(wordMatch);
//                    int index = wordMatch.indexOf(Character.toLowerCase(squaresWithTiles.get(key)));
//                    sb.deleteCharAt(index);
//                    var moveInTiles = wordToTiles(sb.toString());
//                    if (viableVert) {
//                        if (key.y() >= 0) {
//                            Move moveVertical = new Move(moveInTiles, new Coordinate(key.x(), key.y() - index), true);
//                            try {
//                                String finalWord = runner.validateMove(moveVertical);
//                                System.out.println(moveInTiles);
//                                System.out.println(finalWord);
//                                return moveVertical;
//                            } catch (IllegalMoveException e) {}
//                        }
//                    }
//                    if (viableHoriz) {
//                        if (key.x() - index >= 0) {
//                            Move moveHorizontal = new Move(moveInTiles, new Coordinate(key.x() - index, key.y()), false);
//                            try {
//                                String finalWord = runner.validateMove(moveHorizontal);
//                                System.out.println(moveInTiles);
//                                System.out.println(finalWord);
//                                return moveHorizontal;
//                            } catch (IllegalMoveException e) {}
//                        }
//                    }
//                }
//
//                //alphaSearch 8 choose 7 version of the word because this is easy (fori, sb.deleteCharAt(i), etc)...
//
//
//
//                //Check how long the 'runway' is: do we actually have space to play an 8-letter word?
//                //All tiles trick - we could use the alphaSort idea to speed things up? And skip the j loop?
//                //Try to use tile as first letter in word, then second, etc
//                for (int j = 0; j < i; j++) {
//                    Optional<String> result = findPlayableWords(i,
//                            j, squaresWithTiles.get(key));
//                    if (result.isEmpty()) continue;
//                    var sb = new StringBuilder(result.get());
//
//                    //Maybe useful for returning the word to be printed to console??
//                    //String entireWord = sb.toString();
//
//                    String possibleWord = sb.deleteCharAt(j).toString();
//                    //System.out.println("Possible word to play: " + possibleWord);
//                    var moveInTiles = wordToTiles(possibleWord);
//                    if (key.x() - j >= 0) {
//                        Move moveHorizontal = new Move(moveInTiles, new Coordinate(key.x() - j, key.y()), false);
//                        try {
//                            runner.validateMove(moveHorizontal);
//                            System.out.println("j is " + j);
//                            System.out.println("i is " + i);
//                            System.out.println(moveInTiles);
//                            return moveHorizontal;
//                        } catch (IllegalMoveException e) {}
//                    }
//                    if (key.y() - j >= 0) {
//                        Move moveVertical = new Move(moveInTiles, new Coordinate(key.x(), key.y() - j), true);
//                        try {
//                            runner.validateMove(moveVertical);
//                            System.out.println("j is " + j);
//                            System.out.println("i is " + i);
//                            System.out.println(moveInTiles);
//                            return moveVertical;
//                        } catch (IllegalMoveException e) {}
//                    }
//                }
//            }
//        }
//        return null;
//    }

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
