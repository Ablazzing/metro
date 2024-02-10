package org.example.metro.underground;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.ZERO;

/**
 * Касса станции метро
 */
public class Cashier {
    private static final BigDecimal TICKET_FEE = new BigDecimal(20);
    private static final BigDecimal PRICE_RUN = new BigDecimal(5);
    private static final BigDecimal SUBSCRIPTION_PRICE = new BigDecimal(3_000);
    private final Map<LocalDate, BigDecimal> sales = new HashMap<>();
    private final Metro metro;

    protected Cashier(Metro metro) {
        this.metro = metro;
    }

    /**
     * Продажа билета
     */
    protected void sellTicket(String startStation, String finishStation, LocalDate sellDate) {
        int countRuns = metro.countStages(startStation, finishStation);
        BigDecimal ticketPrice = PRICE_RUN.multiply(new BigDecimal(countRuns)).add(TICKET_FEE);
        addValue(sellDate, ticketPrice);
    }

    /**
     * Продажа нового абонемента
     */
    protected Subscription sellNewSubscription(LocalDate startSubscriptionDate) {
        Subscription subscription = metro.addSubscription(startSubscriptionDate);
        addValue(startSubscriptionDate, SUBSCRIPTION_PRICE);
        return subscription;
    }

    /**
     * Продление существующего абонемента
     */
    protected void refreshSubscription(String subscriptionNumber, LocalDate sellDate) {
        metro.refreshSubscription(subscriptionNumber, sellDate);
        addValue(sellDate, SUBSCRIPTION_PRICE);
    }

    private void addValue(LocalDate date, BigDecimal value) {
        sales.put(date, sales.getOrDefault(date, ZERO).add(value));
    }

    protected Map<LocalDate, BigDecimal> getSales() {
        return sales;
    }
}
