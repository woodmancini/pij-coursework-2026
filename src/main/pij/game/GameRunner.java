package pij.game;

import static pij.board.BoardParser.*;
import pij.board.Board;
import pij.tile.Tile;

import java.util.Scanner;

public class GameRunner {
    private Board board;
    private boolean playing = true;
    private boolean openGame;
    private Player Player1;
    private Player Player2;
    private final Scanner scanner = new Scanner(System.in);

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
        board.printBoard();

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
                    return new HumanPlayer();
                }
                case "c" -> {
                    return new CPU();
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

    public Move requestMove(Player player) {
        return null;
    }

    public void updateBoard(Move move) {

    }

    public void isValid(Move move) {
        String word = "";
        for (Tile tile : move.word()) {
            var sb = new StringBuilder();
            sb.append(tile.letter().toLowerCase());
            word = sb.toString();
        }
        // Check word against wordlist.txt
    }
}
