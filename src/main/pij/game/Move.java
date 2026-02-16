package pij.game;

import pij.board.Coordinate;
import pij.tile.Tile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public record Move(List<Tile> word, Coordinate coordinate, boolean vertical) {

    public boolean isValidWord(Move this) {

        //Turn tiles into a String word
        var sb = new StringBuilder();
        for (Tile tile : this.word()) {
            sb.append(tile.getLetter());
        }
        String word = sb.toString().toLowerCase();

        File wordList = new File("resources" + File.separator + "wordlist.txt");
        try (var reader = new BufferedReader(new FileReader(wordList))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.strip().equals(word)) return true;
            }
        } catch (IOException e) {
            System.out.println("Error finding word list file: " + e.getMessage());
        }
        return false;
    }

    public String wordToString() {
        var sb = new StringBuilder();
        for (Tile tile : word) {
            sb.append(tile.getLetter());
        }
        return sb.toString();
    }

}
