package pij.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Object that contains a matrix of Square objects representing the board for a game of ScraeBBKle. Contains a reference
 * to the start square, and flag to indicate if first move is taken or not.
 */
public class Board {

    private boolean firstMove = true;
    private final List<List<Square>> board;
    private final Coordinate startSquare;
    private final int SizeX;
    private final int SizeY;

    public Board(List<List<Square>> board, Coordinate startSquare) {
        this.board = board;
        this.startSquare = startSquare;
        this.SizeX = board.getFirst().size();
        this.SizeY = board.size();
        for (int y = 0; y < SizeY; y++) {
            for (int x = 0; x < SizeX; x++) {
                var square = board.get(y).get(x);
                square.setCoordinate(x,y);
                if (x > 0) square.setLeft(board.get(y).get(x - 1));
                if (y > 0) square.setAbove(board.get(y - 1).get(x));
                if (x < SizeX - 1) square.setRight(board.get(y).get(x + 1));
                if (y < SizeY - 1) square.setBelow(board.get(y + 1).get(x));
            }
        }

    }

    //Just for testing
    public void printCoordinates() {
        for (var row : board) {
            System.out.println();
            for (var column : row) {
                System.out.print(column.getCoordinate().toString() + " ");
            }
        }
        System.out.println();
    }

    public void firstMoveTaken() {
        firstMove = false;
    }

    public boolean isFirstMove() {
        return firstMove;
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
        return board.get(coordinate.y()).get(coordinate.x());
    }

    public Square getSquare(int x, int y) {
        return board.get(y).get(x);
    }

    /**
     * Prints a convenient and readable representation of the board to the console for players to view.
     */
    public void printBoard() {
        String alphaIndices = generateAlphaIndices();
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

    /**
     * Helper method that generates alphabetical indices to be printed at top and bottom of board.
     * @return String representation of alphabetical indices.
     */
    private String generateAlphaIndices() {
        var sb = new StringBuilder("    ");
        for (int i = 0; i < getSizeX(); i++) {
            sb.append(" ")
                    .append(Coordinate.intToChar(i))
                    .append(" ");
        }
        return sb.toString();
    }

    public void printPlayable() {
        for (var row : board) {
            System.out.println();
            for (var column : row) {
                System.out.print(column.getCoordinate().toString() + "=");
                if (column.isPlayable()) System.out.print(" 1 ");
                else System.out.print(" 0 ");
            }
        }
        System.out.println();
    }

    public Square getLeft(Coordinate coord) {
        try {
            return getSquare(coord.x() - 1, coord.y());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Square getRight(Coordinate coord) throws IndexOutOfBoundsException {
            return getSquare(coord.x() + 1, coord.y());
    }

    public Square getAbove(Coordinate coord) {
        try {
            return getSquare(coord.x(), coord.y() - 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Square getBelow(Coordinate coord) throws IndexOutOfBoundsException {
            return getSquare(coord.x(), coord.y() + 1);
    }

//    public boolean hasNeighboursX(Coordinate coord){
//        return getLeft(coord).hasTile() || getRight(coord).hasTile();
//    }
//    public boolean hasNeighboursY(Coordinate coord){
//        return getSquare(coord).
//    }

}
