package org.example.metro.exceptions;

public class LineAlreadyExistsException extends RuntimeException {
    public LineAlreadyExistsException(String lineName) {
        super("Line already exists: " + lineName);
    }
}
