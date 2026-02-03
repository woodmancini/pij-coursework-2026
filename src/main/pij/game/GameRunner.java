package pij.game;

import static pij.board.BoardParser.*;
import static pij.tile.Tile.toTile;

import pij.board.Board;
import pij.board.Coordinate;
import pij.tile.Tile;
import pij.tile.TileBag;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameRunner {
    public static final int TILES_PER_PLAYER = 7;
    private Board board;
    private boolean openGame;
    private Player Player1;
    private Player Player2;
    private final Scanner scanner = new Scanner(System.in);
    private TileBag tileBag = new TileBag();

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
        board.printBoard();

    }

    public void playGame() {
        while (tileBag.tilesRemaining() > 0 && !Player1.getHand().isEmpty() || !Player2.getHand().isEmpty()) {
            Move P1Move = requestMove(Player1);
            if (P1Move.isValidWord() && P1Move.isValidMove()) {
                updateBoard(P1Move);
            }
            Move P2Move = requestMove(Player2);
            if (P2Move.isValidWord() && P2Move.isValidMove()) {
                updateBoard(P1Move);
            }
        }
    }

    // TO DO start position should print in the format d7
    public Move requestMove(Player player) {

        Player otherPlayer = player.getName().equals("Player 1") ? Player2 : Player1;
        board.printBoard();
        System.out.println();
        System.out.printf("Start position: %s%n", board.getStartSquare());

        if (openGame) {
            System.out.printf("""
                    OPEN GAME: %s's tiles:
                    OPEN GAME: %s
                    """, otherPlayer, otherPlayer.printHand());
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

        // Try catch?
        String input = scanner.nextLine().strip();
        return stringToMove(input);

    }

    private boolean confirmOpenGame() {
        while (true) {
            System.out.println("""
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
            System.out.println("""
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

    //Are x and y the right way around when converting "d7" to coordinate?
    public Move stringToMove(String input) {

        String[] inputStrings = input.split(",");
        char[] wordInChar = inputStrings[0].toCharArray();
        List<Tile> wordInTiles = new ArrayList<>();
        try {
            for (char c : wordInChar) {
                wordInTiles.add(toTile(c));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error reading your tile: " + e.getMessage());
        }

        boolean vertical = false;

        String coordinate = inputStrings[1].strip();
        int x = 0, y = 0;
        if (Character.isLetter(coordinate.charAt(0))) {
            vertical = true;
            y = Coordinate.charToInt(coordinate.charAt(0));
            x = Integer.parseInt(coordinate.substring(1)) - 1;
        } else {
            y = Coordinate.charToInt(coordinate.charAt(coordinate.length() - 1));
            x = Integer.parseInt(coordinate.substring(0, coordinate.length() - 1)) - 1;
        }

        return new Move(wordInTiles, new Coordinate(x, y), vertical);
    }

    public void updateBoard(Move move) {
        int x = move.coordinate().x();
        int y = move.coordinate().y();
        for (var tile : move.word()) {
            board.getSquare(x, y).placeTile(tile);
            if (move.vertical()) y++;
            else x++;
        }
    }

}
