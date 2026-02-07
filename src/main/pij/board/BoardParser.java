package pij.board;

import pij.exceptions.BoardParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BoardParser {

    public static final int MIN_COLUMNS = 7;
    public static final int MAX_COLUMNS = 26;
    public static final int MIN_ROWS = 10;
    public static final int MAX_ROWS = 99;

    //Should there be a version that takes in an absolute filepath?
    public static Board parseBoardFromFile(String filePath) {

        File boardFile = new File(System.getProperty("user.dir") + File.separator + filePath);
        List<List<Square>> result = new ArrayList<>();
        List<String> lines = List.of();

        try (BufferedReader reader = new BufferedReader(new FileReader(boardFile))) {
            lines = new ArrayList<>(reader.readAllLines());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Parse first line
        String firstLine = lines.removeFirst().strip();
        int columns = Integer.parseInt(firstLine);
        if ((columns < MIN_COLUMNS) || (columns > MAX_COLUMNS)) {
            throw new BoardParseException("Error: " + columns + " is not a valid number of columns.");
        }

        // Parse second line
        String secondLine = lines.removeFirst().strip();
        int rows = Integer.parseInt(secondLine);
        if ((rows < MIN_ROWS) || (rows > MAX_ROWS)) {
            throw new BoardParseException("Error: " + rows + " is not a valid number of rows.");
        }

        //Parse third line
        String thirdLine = lines.removeFirst().strip().toLowerCase();
        char columnIndex = thirdLine.charAt(0);
        if ((columnIndex < 'a') || (columnIndex > 'z')) {
            throw new BoardParseException("Error setting start square: " + columnIndex + " is not a valid column.");
        }
        int rowIndex = Integer.parseInt(thirdLine.substring(1));
        if ((rowIndex < 1) || (rowIndex > MAX_ROWS)) {
            throw new BoardParseException("Error setting start square: " + rowIndex + " is not a valid row.");
        }
        var startSquare = new Coordinate(Character.getNumericValue(columnIndex) - 10, rowIndex - 1);

        // Parse rest of file
        for (String line : lines) {
            if (line.isBlank()) continue;
            result.add(parseRowFromString(line));
        }

        if (rows != result.size()) {
            throw new BoardParseException("Number of rows does not match file.");
        }

        if (columns != result.getFirst().size()) {
            throw new BoardParseException("Number of columns does not match file.");
        }

        return new Board(result, startSquare);

    }

    public static Board parseBoardFromFile() {
        return parseBoardFromFile("resources" + File.separator + "defaultBoard.txt");
    }

    private static List<Square> parseRowFromString(String input) {
        var result = new ArrayList<Square>();
        input = input.replaceAll("\\s+", "");
        while (!input.isEmpty()) {
            switch (input.charAt(0)) {
                case '.' -> {
                    result.add(new Square());
                    input = input.substring(1);
                }
                case '<' -> {
                    int multiplier = Integer.parseInt(input.substring(1, input.indexOf(">")));
                    result.add(new WordPremiumSquare(multiplier));
                    input = input.substring(input.indexOf(">") + 1);
                }
                case '[' -> {
                    int multiplier = Integer.parseInt(input.substring(1, input.indexOf("]")));
                    result.add(new LetterPremiumSquare(multiplier));
                    input = input.substring(input.indexOf("]") + 1);
                }
                default -> throw new BoardParseException("Cannot parse character: " + String.valueOf(input.charAt(0)));
            }
        }
        return result;
    }
}
