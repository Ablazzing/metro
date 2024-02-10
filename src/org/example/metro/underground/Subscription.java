package org.example.metro.underground;

import java.time.LocalDate;

/**
 * Абонемент в метро
 */
public class Subscription {
    private final String number;
    private LocalDate startDate;

    public Subscription(String number, LocalDate startDate) {
        this.number = number;
        this.startDate = startDate;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "number='" + number + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
