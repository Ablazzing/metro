package org.example.metro.exceptions;

public class StationAlreadyExistsException extends RuntimeException {
    public StationAlreadyExistsException(String stationName) {
        super("Station with this name already exists: " + stationName);
    }
}
