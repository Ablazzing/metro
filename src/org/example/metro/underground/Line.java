package org.example.metro.underground;

import org.example.metro.exceptions.StationNotExistsException;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

public class Line {
    private String color;
    private LinkedList<Station> stations = new LinkedList<>();

    protected Line(String color) {
        this.color = color;
    }

    protected void addStation(Station station) {
        stations.add(station);
    }

    protected Station createFirstStation(String stationName, Set<Line> changeLines) {
        Station station = new Station(stationName, changeLines);
        stations.add(station);
        return station;
    }

    protected Station createLastStation(String stationName, Duration timeToStation, Set<Line> changeLines) {
        Station station = new Station(stationName, changeLines);
        Station prevStation = stations.getLast();
        if (prevStation == null) {
            throw new RuntimeException("Previous station not exists in this line");
        }
        if (prevStation.getNextStation() != null) {
            throw new RuntimeException("Previous station is not last in line");
        }
        prevStation.setTimeToNextStation(timeToStation);
        station.setPrevStation(prevStation);
        stations.add(station);
        return station;
    }

    protected Station createBetweenStation(String newStationName, String prevStationName, Duration timeToStation,
                                        Set<Line> changeLines) {
        Station station = new Station(newStationName, changeLines);
        Station prevStation = stations.stream().filter(e -> e.getName().equals(prevStationName))
                .findFirst().orElseThrow(() -> new StationNotExistsException(prevStationName));
        Station nextStation = prevStation.getNextStation();
        if (nextStation == null) {
            throw new RuntimeException("Station " + prevStationName + " has no next station");
        }
        prevStation.setTimeToNextStation(timeToStation);
        prevStation.setNextStation(station);
        nextStation.setPrevStation(station);

        Duration oldDurationBetweenStations = prevStation.getTimeToNextStation();
        Duration durationToNextStation = oldDurationBetweenStations.minus(timeToStation)
                .plusSeconds(30);
        if (durationToNextStation.getSeconds() <= 30) {
            throw new RuntimeException("Duration between new station and next station less than 30 seconds");
        }
        station.setTimeToNextStation(durationToNextStation);
        station.setPrevStation(prevStation);
        station.setNextStation(nextStation);
        return station;
    }

    public LinkedList<Station> getStations() {
        return stations;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
