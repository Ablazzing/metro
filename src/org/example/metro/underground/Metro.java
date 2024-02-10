package org.example.metro.underground;

import org.example.metro.exceptions.LineNotExistsException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.metro.underground.UndergroundValidatorUtil.checkDuration;
import static org.example.metro.underground.UndergroundValidatorUtil.checkLineIsNotEmpty;
import static org.example.metro.underground.UndergroundValidatorUtil.checkLineNotExist;
import static org.example.metro.underground.UndergroundValidatorUtil.checkNotTheSameStations;
import static org.example.metro.underground.UndergroundValidatorUtil.checkStationNotExists;
import static org.example.metro.underground.util.UndergroundUtil.parseTimeToStation;

/**
 * Метрополитен
 */
public class Metro {
    private static final int LIMIT_SUBSCRIPTIONS = 10_000;
    private static final String TICKET_NUMBER_PATTERN = "a%04d";
    private final String city;
    private final HashSet<MetroLine> metroLines = new HashSet<>();
    private final Map<String, Subscription> subscriptions = new HashMap<>();
    private int countSoldSubscription = 0; //Количество проданных абонементов

    public Metro(String city) {
        Objects.requireNonNull(city);
        this.city = city;
    }

    /**
     * Создание линии
     */
    public MetroLine createLine(LineColor lineColor) {
        Objects.requireNonNull(lineColor);
        checkLineNotExist(metroLines, lineColor.getValue());
        MetroLine metroLine = new MetroLine(lineColor, this);
        metroLines.add(metroLine);
        return metroLine;
    }

    /**
     * Создание первой станции на линии
     */
    public Station createFirstStation(String lineColor,
                                      String stationName,
                                      Set<String> changeLineStations) {
        checkStationNotExists(metroLines, stationName);
        MetroLine metroLine = findLineByColor(lineColor);
        checkLineIsNotEmpty(metroLine);
        if (changeLineStations == null) {
            return metroLine.createFirstStation(stationName);
        }
        return metroLine.createFirstStation(stationName, findStations(changeLineStations));
    }


    /**
     * Создание первой станции на линии
     */
    public Station createFirstStation(String lineColor, String stationName) {
        return createFirstStation(lineColor, stationName, null);
    }

    /**
     * Создание последней станции на линии
     */
    public Station createLastStation(String lineColor,
                                     String stationName,
                                     String timeToStationText,
                                     Set<String> changeLineStations) {
        Duration timeToNextStation = parseTimeToStation(timeToStationText);
        checkDuration(timeToNextStation);
        MetroLine metroLine = findLineByColor(lineColor);
        checkStationNotExists(metroLines, stationName);
        if (changeLineStations == null) {
            return metroLine.createLastStation(stationName, timeToNextStation);
        }
        return metroLine.createLastStation(stationName,
                timeToNextStation,
                findStations(changeLineStations));
    }

    /**
     * Создание последней станции на линии
     */
    public Station createLastStation(String lineColor,
                                     String stationName,
                                     String timeToStationText) {
        return createLastStation(lineColor, stationName, timeToStationText, null);
    }

    /**
     * Подсчет перегонов между станциями
     */
    protected int countStages(String stationStartName, String stationFinishName) {
        Station stationStart = getStationByName(stationStartName);
        Station stationFinish = getStationByName(stationFinishName);
        MetroLine startMetroLine = stationStart.getLine();
        MetroLine finishMetroLine = stationFinish.getLine();
        if (startMetroLine == finishMetroLine) {
            return countStagesOnSameLine(stationStart, stationFinish);
        }
        Station changeLineStationStart = findChangeLineStation(startMetroLine, finishMetroLine);
        Station changeLineStationFinish = findChangeLineStation(finishMetroLine, startMetroLine);
        return countStagesOnSameLine(stationStart, changeLineStationStart)
                + countStagesOnSameLine(stationFinish, changeLineStationFinish);
    }

    /**
     * Обновление абонемента
     */
    protected void refreshSubscription(String subscriptionNumber, LocalDate startSubscriptionDate) {
        if (!subscriptions.containsKey(subscriptionNumber)) {
            throw new RuntimeException("Абонемент не существует");
        }
        subscriptions.get(subscriptionNumber).setStartDate(startSubscriptionDate);
    }

    /**
     * Покупка абонемента
     */
    protected Subscription addSubscription(LocalDate startSubscriptionDate) {
        String subscriptionNumber = generateNewSubscriptionNumber();
        Subscription subscription = new Subscription(subscriptionNumber, startSubscriptionDate);
        return subscriptions.put(subscriptionNumber, subscription);
    }

    /**
     * Печать доходов метро за каждый день
     */
    public void printAllIncomes() {
        System.out.println("Доходы метро по датам");
        metroLines.stream()
                .flatMap(metroLine -> metroLine.getStations().stream())
                .flatMap(station -> station.getCashier().getSales().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        BigDecimal::add))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(System.out::println);
    }

    /**
     * Создание номера абонемента
     */
    private String generateNewSubscriptionNumber() {
        if (countSoldSubscription >= LIMIT_SUBSCRIPTIONS) {
            throw new RuntimeException("Исчерпан лимит количества абонементов");
        }
        countSoldSubscription++;
        return String.format(TICKET_NUMBER_PATTERN, countSoldSubscription);
    }

    /**
     * Поиск станции для пересадки между линиями
     */
    private Station findChangeLineStation(MetroLine metroLineStart, MetroLine metroLineChange) {
        return metroLineStart.getStations().stream()
                .filter(station -> stationHasChangeToLine(station, metroLineChange))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Нет станций с пересадкой на линию: "
                            + metroLineChange.getColor())
                );
    }

    /**
     * У станции есть возможность пересесть на указанную линию
     */
    private boolean stationHasChangeToLine(Station station, MetroLine metroLineChange) {
        return station.getChangeLineStations() != null
                && getChangeLinesFromStation(station).contains(metroLineChange);
    }

    /**
     * Получение линий на пересадку у станции
     */
    private Set<MetroLine> getChangeLinesFromStation(Station station) {
        return station.getChangeLineStations().stream()
                .map(Station::getLine)
                .collect(Collectors.toSet());
    }

    /**
     * Подсчет количества перегонов между станциями в рамках одной линии
     */
    private int countStagesOnSameLine(Station stationStart, Station stationFinish) {
        checkNotTheSameStations(stationStart, stationFinish);

        int nextStation = getStationByName(stationStart,
                stationFinish, Station::getNextStation);
        if (nextStation > 0) {
            return nextStation;
        }

        int prevStation = getStationByName(stationStart,
                stationFinish, Station::getPrevStation);
        if (prevStation > 0) {
            return prevStation;
        }
        throw new RuntimeException(
                "Станции %s %s не на одной линии".formatted(stationStart.getName(), stationFinish.getName())
        );
    }

    /**
     * Подсчет перегонов между станциями в одну сторону
     */
    private int getStationByName(Station stationStart,
                                 Station stationFinish,
                                 Function<Station, Station> getNextStationFunc) {
        Station nextStation = getNextStationFunc.apply(stationStart);
        int count = 0;
        while (true) {
            if (nextStation == stationStart) {
                throw new RuntimeException("Бесконечный цикл поиска пути между станциями %s %s"
                                .formatted(stationStart.getName(), stationFinish.getName()));
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

    /**
     * Получение станции по имени
     */
    public Station getStationByName(String stationName) {
        return metroLines.stream()
                .flatMap(metroLine -> metroLine.getStations().stream())
                .filter(station -> Objects.equals(station.getName(), stationName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Станции с таким именем нет: " + stationName));
    }

    /**
     * Преобразования списка названий станций, в список станций
     */
    private Set<Station> findStations(Set<String> stationNames) {
        return stationNames.stream().map(this::getStationByName).collect(Collectors.toSet());
    }


    /**
     * Получение линии по цвету
     */
    public MetroLine findLineByColor(String lineColor) {
        return metroLines.stream()
                .filter(metroLine -> Objects.equals(metroLine.getColor().getValue(), lineColor))
                .findFirst()
                .orElseThrow(() -> new LineNotExistsException(lineColor));
    }

    @Override
    public String toString() {
        return "Metro{" +
                "city='" + city + '\'' +
                ", lines=" + metroLines +
                '}';
    }
}
