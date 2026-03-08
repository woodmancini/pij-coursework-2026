package pij.game;

import pij.board.Board;

public final class CPU extends Player {

    public CPU(String name, Board board) {
        super(name, board);
    }

    public Move findMove() {

//        To do:
//        Implement the computer player in such a way that it will always make a move with at
//        least one tile whenever such a move is possible with the given tile rack, board state, and
//        word list.
//        Hint: A possible strategy to achieve this goal could be the following:
//        1. For each number n of tiles that could be played from the current tile rack (between
//                at most the number of tiles on the rack and at least 1), for each free position on the
//        board, and for both directions, check whether it is possible to play n tiles there.
//        (Often enough, the answer will be false, e.g., because no connection to the existing
//        crossword on the board would be made. There is no need to check all the possible
//        combinations of n tiles for a position and direction where they cannot be played
//        anyway!)
//        2. For those cases of part (1) where it is indeed possible to play n tiles, go through all
//        permutations of n tiles on your rack. If one of them leads to a valid move for the
//        board and the word list, make the move.
        return null;
    }

}
