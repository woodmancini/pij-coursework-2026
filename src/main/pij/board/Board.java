package pij.board;

import java.util.List;

public class Board {

    private List<List<Square>> board;
    private Coordinate startSquare;

    public Board(List<List<Square>> board, Coordinate startSquare) {
        this.board = board;
        this.startSquare = startSquare;
    }

    public Coordinate getStartSquare() {
        return startSquare;
    }

    public Square getTile(int column, int row) {
        return board.get(row).get(column);
    }

    public void printBoard() {
        String alphaIndices = generateAlphaIndices(board.getFirst().size());
        System.out.println(alphaIndices);
        System.out.println();
        int rowCount = 1;
        for (var row : board) {
            System.out.printf("%-4s", rowCount);
            for (Square square : row) {
                if (square.toString().length() == 1) System.out.printf(" %s ", square);
                else System.out.printf("%3s", square);
            }
            System.out.printf("%4s", rowCount);
            System.out.println();
            rowCount++;
        }
        System.out.println();
        System.out.println(alphaIndices);
    }

    public char intToChar(int i) {
        List<Character> alphabet = List.of(
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        );
        return alphabet.get(i);
    }

    private String generateAlphaIndices(int length) {
        var sb = new StringBuilder("    ");
        for (int i = 0; i < board.getFirst().size(); i++) {
            sb.append(" ")
                    .append(intToChar(i))
                    .append(" ");
        }
        return sb.toString();
    }

}
