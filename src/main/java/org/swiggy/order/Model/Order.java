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
    @Setter
    @NotNull
    @Positive(message = "Restaurant id is required")
    private Long restaurantId;

    @Setter
    @Getter
    @Embedded
    private Money totalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public User getUser() {
        return user;
    }


    public Order() {
    }

    public Order(@Positive(message = "Restaurant id is required") Long restaurantId, User user) {
        this.restaurantId = restaurantId;
        totalAmount = new Money();
        this.user = user;
        this.status = OrderStatus.ACCEPTED;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setId(Long orderId) {
        this.id = orderId;
    }

    public void fulfillOrder() {
        this.status = OrderStatus.DELIVERED;
    }
}
