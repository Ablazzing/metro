package org.example.metro.exceptions;

public class StationNotExistsException extends RuntimeException {
    public StationNotExistsException(String stationName) {
        super("Station not exists by name: " + stationName);
    }
}
