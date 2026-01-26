package pij.board;

import pij.exceptions.BoardParseException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardParserTest {

    @Test
    void parseBoardWith9Rows() {
        assertThrows(BoardParseException.class, () -> BoardParser.parseBoardFromFile("testBoard9Rows.txt"));
    }

    @Test
    void parseBoardWith6Columns() {
        assertThrows(BoardParseException.class, () -> BoardParser.parseBoardFromFile("testBoard6Columns.txt"));
    }

    @Test
    void parseBoardWrongNumberOfRows() {
        assertThrows(BoardParseException.class, () -> BoardParser.parseBoardFromFile("testBoardWrongNumberOfRows.txt"));
    }




}
