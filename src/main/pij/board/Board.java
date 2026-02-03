package pij.board;

import java.util.List;

public class Board {

    private final List<List<Square>> board;
    private final Coordinate startSquare;
    private final int SizeX;
    private final int SizeY;

    public Board(List<List<Square>> board, Coordinate startSquare) {
        this.board = board;
        this.startSquare = startSquare;
        this.SizeX = board.getFirst().size();
        this.SizeY = board.size();
    }

    public int getSizeX() {
        return SizeX;
    }

    public int getSizeY() {
        return SizeY;
    }

    public Coordinate getStartSquare() {
        return startSquare;
    }

    public Square getSquare(Coordinate coordinate) {
        return board.get(coordinate.x()).get(coordinate.y());
    }

    public Square getSquare(int x, int y) {
        return board.get(x).get(y);
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

    private String generateAlphaIndices(int length) {
        var sb = new StringBuilder("    ");
        for (int i = 0; i < board.getFirst().size(); i++) {
            sb.append(" ")
                    .append(Coordinate.intToChar(i))
                    .append(" ");
        }
        return sb.toString();
    }

}
