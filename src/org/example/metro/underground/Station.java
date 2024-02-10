package org.example.metro.underground;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.metro.underground.UndergroundValidatorUtil.checkNonNullValues;

public class Station {
    private final String name;
    private Station prevStation;
    private Station nextStation;
    private Duration timeToNextStation;
    private Set<Station> changeLineStations;
    private final Line line;
    private final Metro metro;
    private final Cashier cashier;

    protected Station(String name, Line line, Metro metro, Set<Station> changeLineStations) {
        checkNonNullValues(name, line, metro);
        this.name = name;
        this.changeLineStations = changeLineStations;
        this.line = line;
        this.metro = metro;
        this.cashier = new Cashier(metro);
    }

    public Line getLine() {
        return line;
    }

    public void saleOneTicket(String stationStart, String stationFinish, LocalDate date) {
        cashier.sellTicket(stationStart, stationFinish, date);
    }

    public Subscription saleSubscription(LocalDate date) {
        return cashier.sellNewSubscription(date);
    }

    public void refreshSubscription(String subscriptionNumber, LocalDate date) {
        cashier.refreshSubscription(subscriptionNumber, date);
    }

    public Set<Station> getChangeLineStations() {
        return changeLineStations;
    }

    public void setChangeLineStations(Set<Station> changeLineStations) {
        this.changeLineStations = changeLineStations;
    }

    public String getName() {
        return name;
    }

    public Station getPrevStation() {
        return prevStation;
    }

    protected void setPrevStation(Station prevStation) {
        this.prevStation = prevStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    protected void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }

    public Duration getTimeToNextStation() {
        return timeToNextStation;
    }

    protected void setTimeToNextStation(Duration timeToNextStation) {
        this.timeToNextStation = timeToNextStation;
    }

    public Cashier getCashier() {
        return cashier;
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
                String.format(", changeLines=%s", getChangeLineColors()) + "'" +
                '}';
    }

    private String getChangeLineColors() {
        if (changeLineStations == null || changeLineStations.isEmpty()) {
            return null;
        }
        return changeLineStations.stream()
                .map(station -> station.getLine().getColor().getValue())
                .collect(Collectors.joining(","));
    }
}
