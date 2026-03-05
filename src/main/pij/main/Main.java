package pij.main;

import pij.game.GameRunner;
import pij.game.HumanPlayer;
import pij.game.Player;
import pij.tile.TestTileBag;
import pij.tile.Tile;
import pij.tile.TileBag;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        var gameRunner = new GameRunner();
        gameRunner.startGame();
        gameRunner.playGame();
        gameRunner.endGame();

    }

}
