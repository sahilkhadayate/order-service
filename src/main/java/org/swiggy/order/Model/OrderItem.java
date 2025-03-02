package org.swiggy.order.Model;

import jakarta.persistence.*;
import org.swiggy.order.DTO.MenuItemDTO;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Embedded
    private Money price;

    private int quantity;

    public OrderItem() {
    }

    public OrderItem(MenuItemDTO menuItemDTO, Order order) {
        this.order = order;
        this.quantity = menuItemDTO.getQuantity();
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }
}
