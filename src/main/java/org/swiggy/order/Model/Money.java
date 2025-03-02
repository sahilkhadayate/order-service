package org.swiggy.order.Model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Currency;

@Embeddable
public class Money {

    @Getter
    private double amount;

    @Getter
    private Currency currency;

    public Money(double amount, Currency currency) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        this.amount = amount;
        this.currency = currency;
    }

    public Money() {
        this.amount=0;
        this.currency = Currency.getInstance("INR");
    }

    public void add(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException();
        }
        this.amount += money.amount;
    }
}
