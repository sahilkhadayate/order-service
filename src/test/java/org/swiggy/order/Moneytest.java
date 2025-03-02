package org.swiggy.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiggy.order.Model.Money;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Moneytest {

    private Money money;

    @BeforeEach
    void setUp() {
        money = new Money(100.0, Currency.getInstance("INR"));
    }

    @Test
    void add_ShouldIncreaseAmount_WhenSameCurrency() {
        // Arrange
        Money additionalMoney = new Money(50.0, Currency.getInstance("INR"));

        // Act
        money.add(additionalMoney);

        // Assert
        assertEquals(150.0, money.getAmount());
    }

    @Test
    void add_ShouldThrowException_WhenDifferentCurrency() {
        // Arrange
        Money differentCurrencyMoney = new Money(50.0, Currency.getInstance("INR"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> money.add(differentCurrencyMoney));
    }
}
