package org.swiggy.order.Model;

import jakarta.persistence.*;
import org.swiggy.order.DTO.MenuItemDTO;


@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Money price;

    private int quantity;

    public OrderItem(MenuItemDTO menuItemDTO) {

        this.quantity = menuItemDTO.getQuantity();
    }

    public OrderItem() {

    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }
}


