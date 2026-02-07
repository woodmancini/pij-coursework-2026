package pij.game;

import static java.util.stream.Collectors.*;
import static pij.board.BoardParser.*;


import pij.board.Board;
import pij.board.Coordinate;
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
            if (P1Move != null) updateBoard((P1Move));
            Move P2Move = requestMove(Player2);
            if (P2Move != null) updateBoard(P2Move);
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


        //Should be its own method that throws a MoveParseException with error message.
        //Would be better as a do-while with one check isValidInput() at the end in the while bit?
        requestInput:
        while (true) {

            String input = scanner.nextLine().strip();

            //Pass the turn
            if (input.equals(",")) {
                System.out.println("The move is: pass!\n");
                return null;
            }

            if (!input.contains(",")) {
                System.out.println("Error: your move doesn't contain a comma. Try again:");
                continue;
            }

            String[] inputStrings = input.split(",");

            //Check that comma splits input into two
            if (inputStrings.length != 2) {
                System.out.println("Error: please include one comma only. Try again:");
                continue;
            }

            String wordString = inputStrings[0].strip();

            //Validate word as String...
            //This won't work because we have to account for words that include tiles already on the board.
            if (!isValidWord(wordString)) {
                System.out.println("That's not a valid word. Please try again!");
                continue;
            }

            //How to check that every letter including wildcards is present only once in hand...
            //Count elements and check word.count <= hand.count?
            //This could be a method
            //Does this work??
            char[] wordInChar = wordString.toCharArray();
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
                    System.out.println("You don't have the necessary tiles for that word. Please try again:");
                    continue requestInput;
                }
            }

            List<Tile> wordInTiles = new ArrayList<>();
            for (char c : wordInChar) {
                wordInTiles.add(new Tile(c));
            }

            boolean vertical = false;

            String coordinate = inputStrings[1].strip();
            int x = 0, y = 0;
            if (Character.isLetter(coordinate.charAt(0))) {
                vertical = true;
                x = Coordinate.charToInt(coordinate.charAt(0));
                y = Integer.parseInt(coordinate.substring(1)) - 1;
            } else {
                x = Coordinate.charToInt(coordinate.charAt(coordinate.length() - 1));
                y = Integer.parseInt(coordinate.substring(0, coordinate.length() - 1)) - 1;
            }

            Move move =  new Move(wordInTiles, new Coordinate(x, y), vertical);

            if (!move.isValidMove()) {
                System.out.println("Error: that's not a valid move. Try again!");
            }

            for (Tile tile : wordInTiles) {
                player.getHand().remove(tile);
            }
            tileBag.deal(player);
            System.out.printf("The move is... letters: %s at position %s%n", inputStrings[0], coordinate);
            System.out.println();
            return move;
        }
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

    public void updateBoard(Move move) {
        int x = move.coordinate().x();
        int y = move.coordinate().y();
        for (var tile : move.word()) {
            board.getSquare(x, y).placeTile(tile);
            if (!move.vertical()) x++;
            else y++;
        }
    }

    public boolean isValidWord(String word) {
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

}
