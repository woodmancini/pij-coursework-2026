package pij.game;

import static java.util.stream.Collectors.*;
import static pij.board.BoardParser.*;


import pij.board.*;
import pij.exceptions.IllegalMoveException;
import pij.tile.TestTileBag;
import pij.tile.Tile;
import pij.tile.TileBag;

import java.io.*;
import java.util.*;

/**
 * Runner class that manages the state of a game of ScraeBBKle while accepting input from user(s). Also provides
 * functionality for validating player moves on the board.
 */
public class GameRunner {

    private Board board;
    private boolean openGame = true;
    private Player Player1;
    private Player Player2;
    private final Scanner scanner = new Scanner(System.in);
    private TileBag tileBag = new TileBag();
    private int turnCount = 0;
    private List<String> playedWords = new ArrayList<>();

    public Player getPlayer1() {
        return Player1;
    }
    public Player getPlayer2() {
        return Player2;
    }
    public void setPlayer1(Player player) {
        this.Player1 = player;
    }
    public void setPlayer2(Player player) {
        this.Player2 = player;
    }
    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }
    public TileBag getTileBag() {
        return tileBag;
    }
    public void setTileBag(TileBag tileBag) {
        this.tileBag = tileBag;
    }
    public void addPlayedWord(String word) {
        playedWords.add(word);
    }

    /**
     * Asks for user input in order to initialise the board, players, and check whether to play open or closed game.
     */
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

    /**
     * Continues to request moves from alternate players until tile bag is empty and one player has played all remaining tiles.
     */
    public void playGame() {
        int passCount = 0;
        Player currentPlayer;
        while (!(Player1.getHand().isEmpty() || Player2.getHand().isEmpty())) {

            //Switch on type? CPU can call findMove() directly without having to request input.
            if (passCount >= 4) return;
            currentPlayer = (turnCount % 2 == 0) ? Player1 : Player2;

            Move move = requestMove(currentPlayer);

            if (move == null) {
                System.out.println("The move is: pass!\n");
                passCount++;
                turnCount++;
                continue;
            }

            currentPlayer.makeMove((move));
            printScores();

            tileBag.deal(currentPlayer);
            turnCount++;
            passCount = 0;
        }
    }

    /**
     * Deducts remaining tiles from player's hand; calculates winner and prints to console.
     * @return int 0 in case of draw, 1 for Player1 victory, 2 for Player2 victory. For testing reasons.
     */
    public int endGame() {

        board.printBoard();

        Player1.deductRemainingTiles();
        Player2.deductRemainingTiles();
        System.out.printf("""
                
                Game over!
                
                Player 1 scored %s points.
                Player 2 scored %s points.%n
                """, Player1.getScore(), Player2.getScore());
        if (Player1.getScore() > Player2.getScore()) {
            System.out.println("Player 1 wins!");
            return 1;
        } else if (Player1.getScore() == Player2.getScore()) {
            System.out.println("It's a draw!");
            return 0;
        } else {
            System.out.println("Player 2 wins!");
            return 2;
        }

    }

    /**
     * Continually requests move input from the given player; parses and validates the move before returning it.
     * @param player The player whose move is being requested.
     * @return The Move object representing the player's (already validated) move.
     */
    public Move requestMove(Player player) {

        Player otherPlayer = player.getName().equals("Player 1") ? Player2 : Player1;
        board.printBoard();
        System.out.println();
        System.out.printf("Start position: %s%n", board.getStartSquare());

        if (openGame) {
            System.out.printf("""
                    OPEN GAME: %s's tiles:
                    OPEN GAME: %s
                    """, otherPlayer.getName(), otherPlayer.handToString());
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
                """, player.getName(), player.handToString());

        if (player instanceof CPU computerPlayer) {
            if (board.isFirstMove()) return computerPlayer.findFirstMove();
            else return computerPlayer.findMoveBetter();
        }

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

                String word = inputStrings[0];
                String coordinate = inputStrings[1];

                player.checkHasTiles(word);


                move = Move.buildMove(word, coordinate);

                finalWord = validateMove(move);

            } catch (IllegalMoveException e) {
                System.out.println(e.getMessage());
                continue;
            }

            System.out.printf("The move is... word: %s at position %s.%n", move.word(), move.getDirectionalCoord());
            addPlayedWord(finalWord);

            return move;

        }
    }

    /**
     * Prints both player's current scores to console.
     */
    private void printScores() {
        System.out.printf("""
                Player 1 score: %s
                Player 2 score: %s
                
                """, Player1.getScore(), Player2.getScore());
    }

    /**
     * Checks player move input is formatted correctly (eg "WORD,d7") and returns an array of two strings split by the comma.
     * @param input Player input as String to be parsed.
     * @return A String[] with two elements: [0] the word to be played and [1] the coordinate to play at.
     * @throws IllegalMoveException in the case of incorrect input format.
     */
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

    /**
     * Asks player whether an open or closed game or is desired.
     * @return true if open game requested, false otherwise.
     */
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

    /**
     * Creates a new player object, either human or CPU, with name "Player " + number that is passed in.
     * @param playerNumber The number to be assigned to the player name.
     * @return A new player HumanPlayer or CPU player object.
     */
    private Player requestPlayer(int playerNumber) {
        while (true) {
            System.out.printf("""
                    Is Player %s a _h_uman player or a _c_omputer player?
                    Please enter your choice (h/c): 
                    """, playerNumber);
            switch (scanner.nextLine()) {
                case "h" -> {
                    return new HumanPlayer("Player " + playerNumber, board);
                }
                case "c" -> {
                    return new CPU("Player " + playerNumber, board, this);
                }
                default -> System.out.println("Please enter a valid choice.");
            }
        }
    }

    /**
     * Asks user if board should be parsed from default or custom, user-provided file.
     * @return new Board object parsed from file.
     */
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

    /**
     * Checks if the given word is valid (ie included in wordlist.txt).
     * @param word The word to be checked.
     * @return Returns true if the given word is valid (ie included in the word list).
     */
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

    /**
     * Checks if the given word is valid and playable given the current state of the board.
     * @param move The move to be validated.
     * @return The completed word as String (including pre-existing tiles on the board).
     * @throws IllegalMoveException If move doesn't fit on board, move doesn't occupy the start square
     * on turn one, or move runs parallel to existing tiles.
     */
    public String validateMove(Move move) throws IllegalMoveException {

        boolean usedStartSquare = false;
        boolean isAdjacentMove = false;
        var sb = new StringBuilder();
        Square currentSquare;
        int x = move.coordinate().x();
        int y = move.coordinate().y();
        int maxY = board.getSizeY() - 1;
        int maxX = board.getSizeX() - 1;
        int i = 0;

        //Does this work?
        try {
            board.getSquare(x,y);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalMoveException(move.coordinate() + " is not a valid square, please try again:");
        }

        String startWord = move.vertical() ? checkAbove(x, y) : checkLeft(x, y);
        if (!startWord.isEmpty()) {
            isAdjacentMove = true;
            sb.append(startWord);
        }

        while (i < move.word().size()) {

            try {
                currentSquare = board.getSquare(x, y);
                if (board.isFirstMove() && board.getStartSquare().equals(new Coordinate(x,y))) usedStartSquare = true;
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalMoveException("Error: not enough squares on the board!");
            }

            if (currentSquare.getTile() != null) {
                sb.append(currentSquare.getTile().getLetter());
                isAdjacentMove = true;
            } else {
                // Check for tiles perpendicular to word (not allowed)
                if (move.vertical()) {
                    if (x == 0) {
                        if (board.getSquare(x + 1, y).getTile() != null) {
                            throw new IllegalMoveException("Error: can't place parallel to an existing word.");
                        }
                    } else if (x == maxX) {
                        if (board.getSquare(x - 1, y).getTile() != null) {
                            throw new IllegalMoveException("Error: can't place parallel to an existing word.");
                        }
                    } else if (board.getSquare(x + 1, y).getTile() != null
                            || board.getSquare(x - 1, y).getTile() != null) {
                        throw new IllegalMoveException("Error: can't place parallel to an existing word.");
                    }
                } else {
                    if (y == 0) {
                        if (board.getSquare(x, y + 1).getTile() != null) {
                            throw new IllegalMoveException("Error: can't place parallel to an existing word.");
                        }
                    } else if (y == maxY) {
                        if (board.getSquare(x, y - 1).getTile() != null) {
                            throw new IllegalMoveException("Error: can't place parallel to an existing word.");
                        }
                    } else if (board.getSquare(x, y + 1).getTile() != null
                            || board.getSquare(x, y - 1).getTile() != null) {
                        throw new IllegalMoveException("Error: can't place parallel to an existing word.");
                    }
                }

                sb.append(move.word().get(i).getLetter());
                i++;
            }

            if (move.vertical()) y++;
            else x++;

        }

        String endWord = move.vertical() ? checkBelow(x, y) : checkRight(x, y);
        if (!endWord.isEmpty()) {
            isAdjacentMove = true;
            sb.append(endWord);
        }

        if (board.isFirstMove() && !usedStartSquare) {
            throw new IllegalMoveException("Error: the first move must use the start square " + board.getStartSquare() + ".");
        }

        if (!board.isFirstMove() && !isAdjacentMove) {
            throw new IllegalMoveException("Error: your word must use a letter already on the board.");
        }

        String wordString = sb.toString();

        //To do: this should happen outside of this method!
        if (!isValidWord(wordString)) {
            throw new IllegalMoveException("Error: " + wordString + " is not a valid word, please try again:");
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
