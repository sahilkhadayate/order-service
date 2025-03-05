package org.swiggy.order.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.swiggy.order.Enum.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Positive(message = "Restaurant id is required")
    private Long restaurantId;

    @Setter
    @Getter
    @Embedded
    private Money totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order() {
    }

    public Order(@Positive(message = "Restaurant id is required") Long restaurantId) {
        this.restaurantId = restaurantId;
        totalAmount = new Money();
        this.status = OrderStatus.ACCEPTED;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    public void setId(Long orderId) {
    this.id = orderId;
    }
}
