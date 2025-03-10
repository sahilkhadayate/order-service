package org.swiggy.order.DTO;

import lombok.Data;
import lombok.Getter;
import org.swiggy.order.Model.Money;

@Getter
@Data
public class MoneyRequest {

    private Double amount;
    private String currency;

    public MoneyRequest(Double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
    public MoneyRequest(Money money) {
        this.amount = money.getAmount();
        this.currency = money.getCurrency().getCurrencyCode();
    }

    public MoneyRequest() {
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "MoneyRequest{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
