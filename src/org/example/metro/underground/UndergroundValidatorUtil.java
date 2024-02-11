package org.example.metro.underground;

import org.example.metro.exceptions.LineAlreadyExistsException;
import org.example.metro.exceptions.StationAlreadyExistsException;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class UndergroundValidatorUtil {

    public static void checkLineIsEmpty(MetroLine metroLine) {
        if (!metroLine.getStations().isEmpty()) {
            throw new RuntimeException("Line already has stations, cannot create first station");
        }
    }

    public static void checkStationNotExists(Collection<MetroLine> metroLines, String stationName) {
        boolean hasStationWithSameName = metroLines.stream()
                .flatMap(e -> e.getStations().stream())
                .anyMatch(e -> e.getName().equals(stationName));
        if (hasStationWithSameName) {
            throw new StationAlreadyExistsException(stationName);
        }
    }

    public static void checkNonNullValues(Object... objects) {
        Arrays.stream(objects).forEach(Objects::requireNonNull);
    }

    public static void checkDuration(Duration timeToStation) {
        if (timeToStation.getSeconds() <= 0) {
            throw new RuntimeException("Time to station must be greater than 0 seconds");
        }
    }

    public static void checkLineNotExist(Collection<MetroLine> metroLines, String lineColor) {
        metroLines.stream()
                .filter(metroLine -> Objects.equals(metroLine.getColor().getValue(), lineColor))
                .findAny()
                .ifPresent((metroLine) -> {
                    throw new LineAlreadyExistsException(lineColor);
                });
    }

    public static void checkNotTheSameStations(Station station1, Station station2) {
        if (station1.equals(station2)) {
            throw new RuntimeException("The stations the same " + station1.getName());
        }
    }

    public static void checkPreviousStationIsLastInLine(Station prevStation) {
        if (prevStation.getNextStation() != null) {
            throw new RuntimeException("Предыдущая станция не последняя в линии");
        }
    }
}
