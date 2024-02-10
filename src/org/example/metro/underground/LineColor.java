package org.example.metro.underground;

/**
 * Цвета линий метро
 */
public enum LineColor {
    RED("Красная"), BLUE("Синяя");
    private final String value;

    LineColor(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Color{" +
                "value='" + value + '\'' +
                '}';
    }
}
