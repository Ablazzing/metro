package org.example.metro;

import org.example.metro.underground.Line;
import org.example.metro.underground.Metro;

public class Runner {
    public static void main(String[] args) {
        Metro metro = new Metro("Пермь");
        Line line = metro.createLine("Красная");
        metro.createFirstStation(line.getColor(), "Спортивная", null);
        metro.createLastStation(line.getColor(), "Медведковская", "2M21S", null);
        metro.createLastStation(line.getColor(), "Молодежная", "1M58S", null);
        metro.createLastStation(line.getColor(), "Пермь 1", "3M", null);
        metro.createLastStation(line.getColor(), "Пермь 2", "2M10S", null);
        metro.createLastStation(line.getColor(), "Дворец Культуры", "4M26S", null);

        Line blueLine = metro.createLine("Синяя");
        //-Пацанская
        // | Перегон 1 минута 30 секунд
        //-Улица Кирова
        // | Перегон 1 минута 47 секунд
        //-Тяжмаш
        // | Перегон 3 минуты 19 секунд
        //-Нижнекамская
        // | Перегон 1 минута 48 секунд
        //-Соборная
        metro.createFirstStation(blueLine.getColor(), "Пацанская", null);
        metro.createLastStation(blueLine.getColor(), "Улица Кирова", "1M30S", null);
        metro.createLastStation(blueLine.getColor(), "Тяжмаш", "1M47S", null);
        metro.createLastStation(blueLine.getColor(), "Нижнекамская", "3M19S", null);
        metro.createLastStation(blueLine.getColor(), "Соборная", "1M48S", null);
        System.out.print(metro);
    }
}
