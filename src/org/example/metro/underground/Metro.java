package org.example.metro.underground;

import org.example.metro.exceptions.LineAlreadyExistsException;
import org.example.metro.exceptions.LineNotExistsException;
import org.example.metro.exceptions.StationAlreadyExistsException;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Metro {
    private final String city;
    private final HashSet<Line> lines = new HashSet<>();

    public Metro(String city) {
        this.city = city;
    }

    public Line createLine(String name) {
        Line line = new Line(name);
        if (lines.contains(line)) {
            throw new LineAlreadyExistsException(name);
        }
        lines.add(line);
        return line;
    }

    public Station createFirstStation(String lineColor, String stationName, Set<String> changeLines) {
        Line line = findLineByColor(lineColor);
        checkStationNotExists(stationName);
        if (!line.getStations().isEmpty()) {
            throw new RuntimeException("Line already has stations, cannot create first station");
        }
        Set<Line> linesByColor = changeLines == null ? null : findLinesByColor(changeLines);
        if (linesByColor != null) {
            deleteLineFromChangeLines(line, linesByColor);
        }
        return line.createFirstStation(stationName, linesByColor);
    }

    public Station createStationBetween(String lineColor, String newStationName, String timeToStationText,
                                        String prevStation, Set<String> changeLines) {
        Duration timeToStation = parseTimeToStation(timeToStationText);
        checkDuration(timeToStation);
        Line line = findLineByColor(lineColor);
        return line.createBetweenStation(newStationName, prevStation, timeToStation, convertChangeLines(line, changeLines));
    }

    private Set<Line> convertChangeLines(Line lineToExclude, Set<String> changeLines) {
        Set<Line> linesByColor = changeLines == null ? null : findLinesByColor(changeLines);
        if (linesByColor != null) {
            deleteLineFromChangeLines(lineToExclude, linesByColor);
        }
        return linesByColor;
    }

    public Station createLastStation(String lineColor, String stationName, String timeToStationText,
                                     Set<String> changeLines) {
        Duration timeToStation = parseTimeToStation(timeToStationText);
        checkDuration(timeToStation);
        Line line = findLineByColor(lineColor);
        checkStationNotExists(stationName);
        return line.createLastStation(stationName, timeToStation, convertChangeLines(line, changeLines));
    }

    private Duration parseTimeToStation(String timeToStationText) {
        return Duration.parse("PT" + timeToStationText);
    }

    private void checkDuration(Duration timeToStation) {
        if (timeToStation.getSeconds() <= 0) {
            throw new RuntimeException("Time to station must be greater than 0 seconds");
        }
    }

    private void deleteLineFromChangeLines(Line selfLine, Set<Line> changeLines) {
        changeLines.remove(selfLine);
    }

    private Set<Line> findLinesByColor(Set<String> lines) {
        return lines.stream().map(this::findLineByColor).collect(Collectors.toSet());
    }

    private Line findLineByColor(String lineColor) {
        return lines.stream().filter(e -> e.getColor().equals(lineColor)).findFirst()
                .orElseThrow(() -> new LineNotExistsException(lineColor));
    }

    private void checkStationNotExists(String stationName) {
        boolean hasStationWithSameName = lines.stream()
                .anyMatch(e -> e.getStations().contains(new Station(stationName)));
        if (hasStationWithSameName) {
            throw new StationAlreadyExistsException(stationName);
        }
    }

    @Override
    public String toString() {
        return "Metro{" +
                "city='" + city + '\'' +
                ", lines=" + lines +
                '}';
    }
}
