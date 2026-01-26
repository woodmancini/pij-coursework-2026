package pij.board;

import java.util.List;

public class Board {

    private List<List<Square>> board;
    private Coordinate startSquare;

    public Board(List<List<Square>> board, Coordinate startSquare) {
        this.board = board;
        this.startSquare = startSquare;
    }

    public void printBoard() {
        for (var row : board) {
            for (Square square : row) {
                if (square.toString().length() == 1) System.out.printf(" %s ", square);
                else System.out.printf("%3s", square);
            }
            System.out.println();
        }
    }

}
