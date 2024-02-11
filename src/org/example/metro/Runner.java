package org.example.metro;

import org.example.metro.underground.Metro;
import org.example.metro.underground.Station;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.example.metro.underground.LineColor.BLUE;
import static org.example.metro.underground.LineColor.RED;

public class Runner {
    public static void main(String[] args) {

        Metro metro = new Metro("Пермь");
        metro.createLine(RED);
        metro.createLine(BLUE);
        metro.createFirstStation(RED.getValue(), "Спортивная");
        metro.createLastStation(RED.getValue(), "Медведковская", "2M21S");
        metro.createLastStation(RED.getValue(), "Молодежная", "1M58S");
        Station perm1 = metro.createLastStation(RED.getValue(), "Пермь 1", "3M");
        metro.createLastStation(RED.getValue(), "Пермь 2", "2M10S");
        metro.createLastStation(RED.getValue(), "Дворец Культуры", "4M26S");

        metro.createFirstStation(BLUE.getValue(), "Пацанская");
        metro.createLastStation(BLUE.getValue(), "Улица Кирова", "1M30S");
        Station tygMash = metro.createLastStation(BLUE.getValue(), "Тяжмаш", "1M47S", Set.of("Пермь 1"));
        metro.createLastStation(BLUE.getValue(), "Нижнекамская", "3M19S");
        metro.createLastStation(BLUE.getValue(), "Соборная", "1M48S");
        perm1.setChangeLineStations(Set.of(tygMash));
        System.out.println(metro);

        perm1.saleSubscription(LocalDate.now());
        perm1.saleSubscription(LocalDate.now().plus(3, ChronoUnit.MONTHS));
        perm1.saleSubscription(LocalDate.now().plus(1, ChronoUnit.MONTHS));
        perm1.saleSubscription(LocalDate.now().plus(2, ChronoUnit.MONTHS));
        perm1.saleSubscription(LocalDate.now());
        metro.printAllIncomes();


    }
}
