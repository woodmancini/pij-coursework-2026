package pij.exceptions;

public class BoardParseException extends RuntimeException {
    private String message;
    public String getMessage() {
        return message;
    }
    public BoardParseException(String message) {
        this.message = message;
    }

}
