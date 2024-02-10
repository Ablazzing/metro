package org.example.metro.underground;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.example.metro.underground.util.UndergroundUtil.getLastValueFromLinkedHashSet;
import static org.example.metro.underground.UndergroundValidatorUtil.checkPreviousStationIsLastInLine;

public class Line {
    private final LineColor lineColor;
    private final LinkedHashSet<Station> stations = new LinkedHashSet<>();
    private final Metro metro;

    protected Line(LineColor lineColor, Metro metro) {
        this.lineColor = lineColor;
        this.metro = metro;
    }

    protected Station createFirstStation(String stationName, Set<Station> changeLineStations) {
        Station station = new Station(stationName, this, metro, changeLineStations);
        stations.add(station);
        return station;
    }

    protected Station createFirstStation(String stationName) {
        return createFirstStation(stationName, null);
    }

    protected Station createLastStation(String stationName, Duration timeToStation) {
        return createLastStation(stationName, timeToStation, null);
    }

    protected Station createLastStation(String stationName,
                                        Duration timeToStation,
                                        Set<Station> changeLineStations) {
        Station prevStation = getLastValueFromLinkedHashSet(stations);
        checkPreviousStationIsLastInLine(prevStation);
        Station newStation = new Station(stationName, this, metro, changeLineStations);
        prevStation.setTimeToNextStation(timeToStation);
        prevStation.setNextStation(newStation);
        newStation.setPrevStation(prevStation);
        stations.add(newStation);
        return newStation;
    }

    protected LinkedHashSet<Station> getStations() {
        return stations;
    }

    protected LineColor getColor() {
        return lineColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(lineColor, line.lineColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineColor);
    }

    @Override
    public String toString() {
        return "Line{" +
                "color='" + lineColor + '\'' +
                ", stations=" + stations +
                '}';
    }
}
