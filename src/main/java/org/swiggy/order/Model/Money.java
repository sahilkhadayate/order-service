package org.swiggy.order.Model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Currency;

@Embeddable
public class Money {

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
    }
}
