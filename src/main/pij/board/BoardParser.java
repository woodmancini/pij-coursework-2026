package pij.board;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardParser {

    public static final int MIN_COLUMNS = 7;
    public static final int MAX_COLUMNS = 26;
    public static final int MIN_ROWS = 10;
    public static final int MAX_ROWS = 99;

    public static Board parseBoardFromFile(String filePath) {

        File boardFile = new File("resources" + File.separator + filePath);
        List<List<Square>> result = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(boardFile))) {
            lines = new ArrayList<>(reader.readAllLines());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Parse first line
        String firstLine = lines.removeFirst().strip();
        int columns = Integer.parseInt(firstLine);
        if ((columns < MIN_COLUMNS) || (columns > MAX_COLUMNS)) {
            System.out.println("Error: " + columns + " is not a valid number of columns.");
            return null;
        }

        // Parse second line
        String secondLine = lines.removeFirst().strip();
        int rows = Integer.parseInt(secondLine);
        if ((rows < MIN_ROWS) || (rows > MAX_ROWS)) {
            System.out.println("Error: " + rows + " is not a valid number of rows.");
            return null;
        }

        //Parse third line
        String thirdLine = lines.removeFirst().strip().toLowerCase();
        char columnIndex = thirdLine.charAt(0);
        if ((columnIndex < 'a') || (columnIndex > 'z')) {
            System.out.println("Error setting start square: " + columnIndex + " is not a valid column.");
            return null;
        }
        int rowIndex = Integer.parseInt(thirdLine.substring(1));
        if ((rowIndex < 1) || (rowIndex > MAX_ROWS)) {
            System.out.println("Error setting start square: " + rowIndex + " is not a valid row.");
            return null;
        }
        var startSquare = new Coordinate(Character.getNumericValue(columnIndex) - 10, rowIndex);

        // Parse rest of file
        for (String line : lines) {
            result.add(parseRowFromString(line));
        }

        //return result;

        return new Board(result, startSquare);

    }

    public static List<Square> parseRowFromString(String input) {
        var result = new ArrayList<Square>();
        input = input.replaceAll("\\s+", "");
        while (!input.isEmpty()) {
            switch (input.charAt(0)) {
                case '.' -> {
                    result.add(new Square('_'));
                    input = input.substring(1);
                }
                case '<' -> {
                    int multiplier = Integer.parseInt(input.substring(1, input.indexOf(">")));
                    result.add(new WordPremiumSquare('_', multiplier));
                    input = input.substring(input.indexOf(">") + 1);
                }
                case '[' -> {
                    int multiplier = Integer.parseInt(input.substring(1, input.indexOf("]")));
                    result.add(new LetterPremiumSquare('_', multiplier));
                    input = input.substring(input.indexOf("]") + 1);
                }
            }
        }
        return result;
    }
}
