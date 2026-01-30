package pij.board;

public record Coordinate(int x, int y) {
    @Override
    public String toString(){
        return Board.intToChar(x) + String.valueOf(y + 1);
    }

}
