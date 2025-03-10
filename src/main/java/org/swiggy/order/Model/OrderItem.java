package org.swiggy.order.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.swiggy.order.DTO.MenuItem;

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

    public OrderItem(MenuItem menuItem, Order order, Money price) {
        this.order = order;
        this.quantity = menuItem.getQuantity();
        this.price = price;
        this.name = menuItem.getName();
    }
//
//    public OrderItem(long id, String name, int quantity, Money price, Order order) {
//        this.id = id;
//        this.name = name;
//        this.quantity = quantity;
//        this.price = price;
//        this.order = order;
//    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
