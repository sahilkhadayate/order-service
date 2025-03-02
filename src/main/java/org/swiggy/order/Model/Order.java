package org.swiggy.order.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive(message = "Restaurant id is required")
    private Long restaurantId;

    public Order() {
    }

    public Order(@Positive(message = "Restaurant id is required") Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getId() {
        return id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }
}
