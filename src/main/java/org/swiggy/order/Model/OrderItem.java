package org.swiggy.order.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.swiggy.order.DTO.MenuItemDTO;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Getter
    @Embedded
    private Money price;

    @Getter
    private int quantity;

    @Setter
    @Getter
    private String name;

    public OrderItem() {

    }

    public OrderItem(MenuItemDTO menuItemDTO, Order order, Money price) {
        this.order = order;
        this.quantity = menuItemDTO.getQuantity();
        this.price = price;
    }

    public OrderItem(long l, String name1, int i, Money inr, Order order) {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
