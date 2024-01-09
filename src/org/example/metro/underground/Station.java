package org.example.metro.underground;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

public class Station {
    private String name;
    private Station prevStation;
    private Station nextStation;
    private Duration timeToNextStation;
    private Set<Line> changeLines;

    public Station(String name) {
        this.name = name;
    }

    public Station(String name, Set<Line> changeLines) {
        this.name = name;
        this.changeLines = changeLines;
    }

    public Station(String name, Station prevStation, Station nextStation, Duration timeToNextStation) {
        this.name = name;
        this.prevStation = prevStation;
        this.nextStation = nextStation;
        this.timeToNextStation = timeToNextStation;
    }

    public Set<Line> getChangeLines() {
        return changeLines;
    }

    public void setChangeLines(Set<Line> changeLines) {
        this.changeLines = changeLines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Station getPrevStation() {
        return prevStation;
    }

    public void setPrevStation(Station prevStation) {
        this.prevStation = prevStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }

    public Duration getTimeToNextStation() {
        return timeToNextStation;
    }

    public void setTimeToNextStation(Duration timeToNextStation) {
        this.timeToNextStation = timeToNextStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                '}';
    }
}
