package org.example.metro.underground;

import org.example.metro.exceptions.LineNotExistsException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static org.example.metro.underground.util.UndergroundUtil.parseTimeToStation;
import static org.example.metro.underground.UndergroundValidatorUtil.*;

public class Metro {
    public static final int LIMIT_SUBSCRIPTIONS = 10_000;
    private final String city;
    private final HashSet<Line> lines = new HashSet<>();
    private final Map<String, Subscription> subscriptions = new HashMap<>();
    private int countSoldSubscription = 0;

    public Metro(String city) {
        Objects.requireNonNull(city);
        this.city = city;
    }

    public Line createLine(LineColor lineColor) {
        Objects.requireNonNull(lineColor);
        checkLineNotExist(lines, lineColor.getValue());
        Line line = new Line(lineColor, this);
        lines.add(line);
        return line;
    }

    public Station createFirstStation(String lineColor,
                                      String stationName,
                                      Set<String> changeLineStations) {
        checkStationNotExists(lines, stationName);
        Line line = findLineByColor(lineColor);
        checkLineIsNotEmpty(line);
        if (changeLineStations == null) {
            return line.createFirstStation(stationName);
        }
        return line.createFirstStation(stationName, findStations(changeLineStations));
    }

    public Station createFirstStation(String lineColor, String stationName) {
        return createFirstStation(lineColor, stationName, null);
    }

    public Station createLastStation(String lineColor,
                                     String stationName,
                                     String timeToStationText,
                                     Set<String> changeLineStations) {
        Duration timeToNextStation = parseTimeToStation(timeToStationText);
        checkDuration(timeToNextStation);
        Line line = findLineByColor(lineColor);
        checkStationNotExists(lines, stationName);
        if (changeLineStations == null) {
            return line.createLastStation(stationName, timeToNextStation);
        }
        return line.createLastStation(stationName,
                timeToNextStation,
                findStations(changeLineStations));
    }

    public Station createLastStation(String lineColor,
                                     String stationName,
                                     String timeToStationText) {
        return createLastStation(lineColor, stationName, timeToStationText, null);
    }

    public int countStages(String stationStartName, String stationFinishName) {
        Station stationStart = countRunsBetweenStationHelper(stationStartName);
        Station stationFinish = countRunsBetweenStationHelper(stationFinishName);
        Line startLine = stationStart.getLine();
        Line finishLine = stationFinish.getLine();
        if (startLine == finishLine) {
            return countStagesOnSameLine(stationStart, stationFinish);
        }
        Station changeLineStationStart = findChangeLineStation(startLine, finishLine);
        Station changeLineStationFinish = findChangeLineStation(finishLine, startLine);
        return countStagesOnSameLine(stationStart, changeLineStationStart)
                + countStagesOnSameLine(stationFinish, changeLineStationFinish);
    }

    public void refreshSubscription(String subscriptionNumber, LocalDate startSubscriptionDate) {
        if (!subscriptions.containsKey(subscriptionNumber)) {
            throw new RuntimeException("Абонемент не существует");
        }
        subscriptions.get(subscriptionNumber).setStartDate(startSubscriptionDate);
    }

    public Subscription addSubscription(LocalDate startSubscriptionDate) {
        String subscriptionNumber = generateNewSubscriptionNumber();
        Subscription subscription = new Subscription(subscriptionNumber, startSubscriptionDate);
        return subscriptions.put(subscriptionNumber, subscription);
    }

    public void printAllIncomes() {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .map(station -> station.getCashier().getSales())
                .flatMap(map -> map.entrySet().stream())
                .peek(entry -> System.out.println(entry + " 1") )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        BigDecimal::add))
                .entrySet()
                .stream().peek(entry -> System.out.println(entry + " 2"))
                .sorted(Map.Entry.comparingByKey())
                .forEach(ingnored -> {});
    }

    private String generateNewSubscriptionNumber() {
        if (countSoldSubscription >= LIMIT_SUBSCRIPTIONS) {
            throw new RuntimeException("Исчерпан лимит количества абонементов");
        }
        countSoldSubscription++;
        return String.format("a%04d", countSoldSubscription);
    }

    private Station findChangeLineStation(Line lineStart, Line lineChange) {
        return lineStart.getStations().stream()
                .filter(station -> stationHasChangeToLine(station, lineChange))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Нет станций с пересадкой на линию: "
                            + lineChange.getColor())
                );
    }

    private boolean stationHasChangeToLine(Station station, Line lineChange) {
        return station.getChangeLineStations() != null
                && getChangeLinesFromStation(station).contains(lineChange);
    }

    private Set<Line> getChangeLinesFromStation(Station station) {
        return station.getChangeLineStations().stream()
                .map(Station::getLine)
                .collect(Collectors.toSet());
    }

    private int countStagesOnSameLine(Station stationStart, Station stationFinish) {
        checkNotTheSameStations(stationStart, stationFinish);

        int nextStation = countRunsBetweenStationHelper(stationStart,
                stationFinish, Station::getNextStation);
        if (nextStation > 0) {
            return nextStation;
        }

        int prevStation = countRunsBetweenStationHelper(stationStart,
                stationFinish, Station::getPrevStation);
        if (prevStation > 0) {
            return prevStation;
        }
        throw new RuntimeException("Станции не на одной линии");
    }

    private int countRunsBetweenStationHelper(Station stationStart,
                                              Station stationFinish,
                                              Function<Station, Station> getNextStationFunc) {
        Station nextStation = getNextStationFunc.apply(stationStart);
        int count = 0;
        while (true) {
            if (nextStation == stationStart) {
                throw new RuntimeException("Бесконечный цикл поиска станции");
            }
            count++;
            if (nextStation == null) {
                return -1;
            }
            if (nextStation == stationFinish) {
                return count;
            }
            nextStation = getNextStationFunc.apply(stationStart);
        }
    }

    private Station countRunsBetweenStationHelper(String stationName) {
        return lines.stream()
                .flatMap(line -> line.getStations().stream())
                .filter(station -> Objects.equals(station.getName(), stationName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Station not found"));
    }

    private Set<Station> findStations(Set<String> stationNames) {
        return stationNames.stream().map(this::countRunsBetweenStationHelper).collect(Collectors.toSet());
    }

    private Line findLineByColor(String lineColor) {
        return lines.stream()
                .filter(line -> Objects.equals(line.getColor().getValue(), lineColor))
                .findFirst()
                .orElseThrow(() -> new LineNotExistsException(lineColor));
    }

    @Override
    public String toString() {
        return "Metro{" +
                "city='" + city + '\'' +
                ", lines=" + lines +
                '}';
    }
}
