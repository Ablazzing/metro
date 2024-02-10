package org.example.metro.underground;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.example.metro.underground.UndergroundValidatorUtil.checkPreviousStationIsLastInLine;
import static org.example.metro.underground.util.UndergroundUtil.getLastValueFromLinkedHashSet;

/**
 * Линия метро
 */
public class MetroLine {
    private final LineColor lineColor;
    private final LinkedHashSet<Station> stations = new LinkedHashSet<>();
    private final Metro metro;

    protected MetroLine(LineColor lineColor, Metro metro) {
        this.lineColor = lineColor;
        this.metro = metro;
    }

    /**
     * Создание первой станции на линии
     */
    protected Station createFirstStation(String stationName, Set<Station> changeLineStations) {
        Station station = new Station(stationName, this, metro, changeLineStations);
        stations.add(station);
        return station;
    }

    /**
     * Создание первой станции на линии
     */
    protected Station createFirstStation(String stationName) {
        return createFirstStation(stationName, null);
    }

    /**
     * Создание последней станции на линии
     */
    protected Station createLastStation(String stationName, Duration timeToStation) {
        return createLastStation(stationName, timeToStation, null);
    }

    /**
     * Создание последней станции на линии
     */
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
        MetroLine metroLine = (MetroLine) o;
        return Objects.equals(lineColor, metroLine.lineColor);
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
