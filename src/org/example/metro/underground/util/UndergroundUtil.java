package org.example.metro.underground.util;

import java.time.Duration;
import java.util.LinkedHashSet;

public class UndergroundUtil {
    public static Duration parseTimeToStation(String timeToStationText) {
        return Duration.parse("PT" + timeToStationText);
    }

    public static <T> T getLastValueFromLinkedHashSet(LinkedHashSet<T> linkedHashSet) {
        return linkedHashSet.stream()
                .reduce((a, b) -> b)
                .orElseThrow(() -> new RuntimeException("LinkedHashSet is empty"));
    }

}
