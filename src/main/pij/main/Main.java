package pij.main;

import pij.board.Board;
import pij.board.Square;
import pij.game.GameRunner;
import pij.tile.Tile;
import pij.tile.TileBag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pij.board.BoardParser.*;

public class Main {

    public static void main(String[] args) {

        var gameRunner = new GameRunner();
        //gameRunner.startGame();
        List<Tile> tiles = new TileBag().getContents();
        Map<Tile, Integer> tileCount = new HashMap<>();
        for (Tile tile: tiles) {
            System.out.println(tile);
        }


    }

}
