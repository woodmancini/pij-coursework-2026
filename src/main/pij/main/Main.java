package pij.main;

import pij.board.BoardParser;
import pij.exceptions.IllegalMoveException;
import pij.game.GameRunner;
import pij.tile.Tile;

public class Main {

    public static void main(String[] args) {

        var gameRunner = new GameRunner();
        gameRunner.setBoard(BoardParser.parseBoardFromFile());
        char[] aardvarks = "AARDVARKS".toCharArray();
        try{
        gameRunner.validateMove(gameRunner.buildMove(aardvarks,"d7"));}
        catch (IllegalMoveException e) {
            System.out.println(e.getMessage());
        }
//        gameRunner.startGame();
//        gameRunner.playGame();

    }

}
