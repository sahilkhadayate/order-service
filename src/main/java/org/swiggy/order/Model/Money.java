package org.swiggy.order.Model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Currency;
import java.util.Objects;

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

    public Money add(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException();
        }
        this.amount += money.amount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Double.compare(money.amount, amount) == 0 &&
                (Objects.equals(currency.getCurrencyCode(), money.currency.getCurrencyCode()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
