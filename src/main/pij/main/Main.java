package pij.main;
import pij.board.Board;
import pij.board.Square;

import java.util.Arrays;
import java.util.List;

import static pij.board.BoardParser.*;

public class Main {
    public static void main(String[] args) {

        Board board = parseBoardFromFile("defaultboard.txt");
//        for (var list : board) {
//            System.out.println(list);
//        }
        board.printBoard();

    }



}
