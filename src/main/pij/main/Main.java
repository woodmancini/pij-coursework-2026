package pij.main;

import pij.board.Board;
import pij.board.Coordinate;
import pij.board.Square;
import pij.game.GameRunner;
import pij.game.Move;
import pij.tile.Tile;
import pij.tile.TileBag;

import java.util.*;

import static pij.board.BoardParser.*;

public class Main {

    public static void main(String[] args) {

        var gameRunner = new GameRunner();
//        gameRunner.startGame();

        Move move = new Move(List.of(Tile.A, Tile.P, Tile.P, Tile.L, Tile.E), new Coordinate(5, 5));
        System.out.println(move.isValidWord());

    }

}
