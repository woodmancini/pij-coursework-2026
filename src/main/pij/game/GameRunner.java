package pij.game;

import static java.util.stream.Collectors.*;
import static pij.board.BoardParser.*;


import pij.board.*;
import pij.exceptions.IllegalMoveException;
import pij.tile.Tile;
import pij.tile.TileBag;

import java.io.*;
import java.util.*;

public class GameRunner {

    public static final int TILES_PER_PLAYER = 7;
    private Board board;
    private boolean openGame;
    private Player Player1;
    private Player Player2;
    private final Scanner scanner = new Scanner(System.in);
    private TileBag tileBag = new TileBag();
    private boolean isFirstMove = true;

    public void firstMoveTaken() {
        this.isFirstMove = false;
    }

    public Board getBoard() {
        return board;
    }

    public void startGame() {

        System.out.println("""
                ============                     ============
                ============ S c r a e B B K l e ============
                ============                     ============
                """);

        board = requestBoard();
        Player1 = requestPlayer(1);
        Player2 = requestPlayer(2);
        openGame = confirmOpenGame();
        tileBag.deal(Player1);
        tileBag.deal(Player2);

    }

    public void playGame() {
        while (tileBag.tilesRemaining() > 0 && !Player1.getHand().isEmpty() || !Player2.getHand().isEmpty()) {
            Move P1Move = requestMove(Player1);
            if (P1Move != null) {
                Player1.updateScore(makeMove((P1Move)));
                printScores();
            }

            Move P2Move = requestMove(Player2);
            if (P2Move != null) {
                Player2.updateScore(makeMove(P2Move));
                printScores();
            }
        }
    }

    public Move requestMove(Player player) {

        Player otherPlayer = player.getName().equals("Player 1") ? Player2 : Player1;
        board.printBoard();
        System.out.println();
        System.out.printf("Start position: %s%n", board.getStartSquare());

        if (openGame) {
            System.out.printf("""
                    OPEN GAME: %s's tiles:
                    OPEN GAME: %s
                    """, otherPlayer.getName(), otherPlayer.printHand());
        }

        System.out.printf("""
                It's your turn, %s! Your tiles:
                %s
                Please enter your move in the format: "word,square" (without the quotes)
                For example, for suitable tile rack and board configuration, a downward move
                could be "HI,f4" and a rightward move could be "HI,4f".
                In the word, upper-case letters are standard tiles and lower-case letters
                are wildcards.
                Entering "," passes the turn.
                """, player.getName(), player.printHand());

        while (true) {

            String input = scanner.nextLine().replaceAll("\\s+", "");

            if (input.equals(",")) {
                System.out.println("The move is: pass!\n");
                return null;
            }

            Move move;
            String finalWord;

            try {

                String[] inputStrings = validateMoveInput(input);

                char[] wordInChar = inputStrings[0].toCharArray();
                String coordinate = inputStrings[1];

                checkPlayerHasTiles(wordInChar, player);

                move = buildMove(wordInChar, coordinate);

                finalWord = validateMove(move);

            } catch (IllegalMoveException e) {
                System.out.println(e.getMessage());
                continue;
            }

            for (Tile tile : move.word()) {
                player.getHand().remove(tile);
            }
            tileBag.deal(player);

            System.out.printf("The move is... word: %s at position %s.%n", finalWord, move.coordinate());
            return move;

        }
    }

    private Move buildMove(char[] wordInChar, String coordinate) throws IllegalMoveException {

        List<Tile> wordInTiles = new ArrayList<>();
        boolean vertical = false;
        int x, y, length = coordinate.length();

        for (char c : wordInChar) {
            wordInTiles.add(new Tile(c));
        }

        // What error does this throw? Can I catch it?
        if (Character.isLetter(coordinate.charAt(0))) {
            vertical = true;
            x = Coordinate.charToInt(coordinate.charAt(0));
            y = Integer.parseInt(coordinate.substring(1)) - 1;
        } else if (Character.isLetter(coordinate.charAt(length - 1))) {
            x = Coordinate.charToInt(coordinate.charAt(length - 1));
            y = Integer.parseInt(coordinate.substring(0, length - 1)) - 1;
        } else throw new IllegalMoveException(coordinate + "is not a valid square, please try again:");

        return new Move(wordInTiles, new Coordinate(x, y), vertical);

    }

    private void printScores() {
        System.out.printf("""
                Player 1 score: %s
                Player 2 score: %s
                
                """, Player1.getScore(), Player2.getScore());
    }

    private String[] validateMoveInput(String input) throws IllegalMoveException {

        if (!input.contains(",")) {
            throw new IllegalMoveException("Error: your move doesn't contain a comma. Try again:");
        }

        String[] inputStrings = input.split(",");

        //Check that comma splits input into two
        if (inputStrings.length != 2) {
            throw new IllegalMoveException("Error: please include one comma only. Try again:");
        }

        return inputStrings;
    }

    private void checkPlayerHasTiles(char[] wordInChar, Player player) throws IllegalMoveException {

        Map<Character, Integer> letterCount = new HashMap<>();

        for (char c : wordInChar) {
            if (Character.isLowerCase(c)) {
                letterCount.put('_', letterCount.getOrDefault('_',0) + 1);
            } else {
                letterCount.put(c, letterCount.getOrDefault(c,0) + 1);
            }
        }

        Map<Character, Integer> tileCount = player.getHand().stream()
                .collect(groupingBy(Tile::getLetter, summingInt(tile -> 1)));

        for (Character key : letterCount.keySet()) {
            if (!tileCount.containsKey(key) || letterCount.get(key) > tileCount.get(key)) {
                throw new IllegalMoveException("You don't have the necessary tiles for that word. Please try again:");
            }
        }

    }

    private boolean confirmOpenGame() {
        while (true) {
            System.out.print("""
                    Would you like to play an _o_pen or a _c_losed game?
                    Please enter your choice (o/c): 
                    """);
            switch (scanner.nextLine()) {
                case "o" -> {
                    return true;
                }
                case "c" -> {
                    return false;
                }
                default -> System.out.println("Please enter a valid choice.");
            }
        }
    }

    private Player requestPlayer(int playerNumber) {
        while (true) {
            System.out.printf("""
                    Is Player %s a _h_uman player or a _c_omputer player?
                    Please enter your choice (h/c): 
                    """, playerNumber);
            switch (scanner.nextLine()) {
                case "h" -> {
                    return new HumanPlayer("Player " + playerNumber);
                }
                case "c" -> {
                    return new CPU("Player " + playerNumber);
                }
                default -> System.out.println("Please enter a valid choice.");
            }
        }
    }

    public Board requestBoard() {
        while (true) {
            System.out.print("""
                    Would you like to _l_oad a board or use the _d_efault board?
                    Please enter your choice (l/d): 
                    """);
            switch (scanner.nextLine()) {
                case "d" -> {
                    return parseBoardFromFile();
                }
                case "l" -> {
                    System.out.println("Please enter the name of your custom board file: ");
                    String fileName = scanner.nextLine();
                    return parseBoardFromFile(fileName);
                }
                default -> System.out.println("Please enter a valid choice.");
            }
        }
    }

    // Place tiles on the board and return score
    public int makeMove(Move move) {
        if (isFirstMove) firstMoveTaken();
        int score = 0;
        int wordMultiplier = 1;
        int x = move.coordinate().x();
        int y = move.coordinate().y();
        int i = 0;

        // Add scores of tiles to the left/above

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
        // Add scores of tiles to the right

        return score * wordMultiplier;
    }

    private boolean isValidWord(String word) {
        File wordList = new File(System.getProperty("user.dir") + File.separator +
                "resources" + File.separator + "wordlist.txt");
        word = word.toLowerCase().strip();
        try (var reader = new BufferedReader(new FileReader(wordList))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.strip().equals(word)) return true;
            }
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }
        return false;
    }

    public String validateMove(Move move) throws IllegalMoveException {
        //go through squares, appending letters to a StringBuilder, until we run out of square or tiles in move
        var sb = new StringBuilder();
        int x = move.coordinate().x();
        int y = move.coordinate().y();
        Square currentSquare;
        int i = 0;
        var coordinatesVisited = new ArrayList<Coordinate>();

        //Currently doesn't add score, need to update makeMove()
        if (move.vertical()) sb.append(checkAbove(x, y));
        else sb.append(checkLeft(x,y));

        while (i < move.word().size()) {

            try {
                currentSquare = board.getSquare(x, y);
                coordinatesVisited.add(new Coordinate(x, y));
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalMoveException("Error: not enough squares on the board!");
            }

            if (currentSquare.getTile() != null) {
                sb.append(currentSquare.getTile().getLetter());
                //add the tile on the board to the word
            } else {
                sb.append(move.word().get(i).getLetter());
                i++;
            }

            if (!move.vertical()) x++;
            else y++;

        }

        //Need to replace this with checkRight() and checkBelow()
        // Check if there's a tile on the board at the end of the word
        if (move.vertical() && y < board.getSizeY()) {
            if (board.getSquare(x, y).getTile() != null) {
                sb.append(board.getSquare(x, y).getTile().getLetter());
            }
        } else if (x < board.getSizeX()) {
            if (board.getSquare(x, y).getTile() != null) {
                sb.append(board.getSquare(x, y).getTile().getLetter());
            }
        }

        if (isFirstMove && !coordinatesVisited.contains(board.getStartSquare())) {
            throw new IllegalMoveException("Error: the first move must use the start square " + board.getStartSquare() + ".");
        }

        String wordString = sb.toString();
        if (!isValidWord(wordString)) {
            throw new IllegalMoveException("Error: that's not a valid word, please try again:");
        }

        return wordString;

    }

    /**
     * Checks if letter(s) exist on board above start square, and returns them as a String.
     */
    private String checkLeft(int x, int y) {
        x = x - 1;
        var result = new StringBuilder();
        while (x >= 0 && board.getSquare(x, y).getTile() != null)  {
            result.append(board.getSquare(x, y).getTile().getLetter());
            x--;
        }
        return result.reverse().toString();
    }

    /**
     * Checks if letter(s) exist on board right of start square, and returns them as a String.
     * @param x x co-ordinate of start square.
     * @param y y co-ordinate of start square.
     * @return A String containing letter(s) to the right of start square.
     */
    private String checkRight(int x, int y) {
        x = x + 1;
        var result = new StringBuilder();
        while (x < board.getSizeX() && board.getSquare(x, y).getTile() != null)  {
            result.append(board.getSquare(x, y).getTile().getLetter());
            x++;
        }
        return result.reverse().toString();
    }

    /**
     * Checks if letter(s) exist on board below start square, and returns them as a String.
     * @param x x co-ordinate of start square.
     * @param y y co-ordinate of start square.
     * @return A String containing letter(s) below start square.
     */
    private String checkBelow(int x, int y) {
        y = + 1;
        var result = new StringBuilder();
        while (y < board.getSizeY() && board.getSquare(x, y).getTile() != null)  {
            result.append(board.getSquare(x, y).getTile().getLetter());
            y++;
        }
        return result.reverse().toString();
    }

    /**
     * Checks if letter(s) exist on board above start square, and returns them as a String.
     */
    private String checkAbove(int x, int y) {
        y = y - 1;
        var result = new StringBuilder();
        while (y >= 0 && board.getSquare(x, y).getTile() != null)  {
            result.append(board.getSquare(x, y).getTile().getLetter());
            y--;
        }
        return result.reverse().toString();
    }
}
