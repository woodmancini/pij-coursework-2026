package pij.main;

import pij.game.GameRunner;
import pij.game.HumanPlayer;
import pij.game.Player;
import pij.tile.TestTileBag;
import pij.tile.Tile;
import pij.tile.TileBag;

import java.util.List;

public class Main {

    public static void main(String[] args) {


        TileBag testTileBag = new TestTileBag();
        var gameRunner = new GameRunner();
        gameRunner.startGame();

        gameRunner.setTileBag(testTileBag);
        gameRunner.Player1.getHand().clear();
        gameRunner.Player2.getHand().clear();
        gameRunner.tileBag.deal(gameRunner.Player1, List.of(new Tile('H'), new Tile('E'), new Tile('L'),
                new Tile('L'), new Tile('O'), new Tile('N'), new Tile('D')));
        gameRunner.tileBag.deal(gameRunner.Player2, List.of(new Tile('G'), new Tile('O'), new Tile('O'),
                new Tile('D'), new Tile('B'), new Tile('Y'), new Tile('E')));

        gameRunner.playGame();
        gameRunner.endGame();
    }

}
