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
import static pij.tile.Tile.toTile;

public class Main {

    public static void main(String[] args) {

        var gameRunner = new GameRunner();
        gameRunner.startGame();
        //gameRunner.playGame();

        //Should x and y be the other way around?
        System.out.println(gameRunner.getBoard().getSquare(0,1));

    }

}
