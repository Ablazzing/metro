package org.example.metro.exceptions;

public class LineNotExistsException extends RuntimeException {
    public LineNotExistsException(String lineColor) {
        super("Line with this color not exists: " + lineColor);
    }
}
