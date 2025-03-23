package org.swiggy.order.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.swiggy.order.Enum.OrderStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Setter
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


    @Getter
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Setter
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();


    public User getUser() {
        return user;
    }


    public Order() {
    }

    public Order(@Positive(message = "Restaurant id is required") Long restaurantId, User user) {
        this.restaurantId = restaurantId;
        totalAmount = new Money();
        this.user = user;
        this.status = OrderStatus.PROCESSING;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public void acceptOrder(){
        this.status = OrderStatus.ACCEPTED;
    }

    public void fulfillOrder() {
        this.status = OrderStatus.DELIVERED;
    }
}
