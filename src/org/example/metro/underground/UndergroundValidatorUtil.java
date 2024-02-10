package org.example.metro.underground;

import org.example.metro.exceptions.LineAlreadyExistsException;
import org.example.metro.exceptions.StationAlreadyExistsException;
import org.example.metro.underground.Line;
import org.example.metro.underground.Station;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class UndergroundValidatorUtil {

    public static void checkLineIsNotEmpty(Line line) {
        if (!line.getStations().isEmpty()) {
            throw new RuntimeException("Line already has stations, cannot create first station");
        }
    }

    public static void checkStationNotExists(Collection<Line> lines, String stationName) {
        boolean hasStationWithSameName = lines.stream()
                .flatMap(e -> e.getStations().stream())
                .anyMatch(e -> e.getName().equals(stationName));
        if (hasStationWithSameName) {
            throw new StationAlreadyExistsException(stationName);
        }
    }

    public static void checkNonNullValues(Object... objects) {
        for (Object object : objects) {
            Objects.requireNonNull(object);
        }
    }

    public static void checkDuration(Duration timeToStation) {
        if (timeToStation.getSeconds() <= 0) {
            throw new RuntimeException("Time to station must be greater than 0 seconds");
        }
    }

    public static void checkLineNotExist(Collection<Line> lines, String lineColor) {
        lines.stream()
                .filter(line -> Objects.equals(line.getColor().getValue(), lineColor))
                .findAny()
                .ifPresent((line) -> { throw new LineAlreadyExistsException(lineColor); });
    }

    public static void checkNotTheSameStations(Station station1, Station station2) {
        if (station1.equals(station2)) {
            throw new RuntimeException("The stations the same " + station1.getName());
        }
    }

    public static void checkPreviousStationNonNull(Station prevStation) {
        if (prevStation == null) {
            throw new RuntimeException("Previous station not exists in this line");
        }
    }

    public static void checkPreviousStationIsLastInLine(Station prevStation) {
        if (prevStation.getNextStation() != null) {
            throw new RuntimeException("Previous station is not last in line");
        }
    }
}
