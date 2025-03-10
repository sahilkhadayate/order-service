package org.swiggy.order.DTO;

import lombok.Data;


@Data
public class MenuItem {

    private Long id;
    private String name;
    private int quantity;

    public MenuItem(Long id, int quantity, String name) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
    }

    public MenuItem() {

    }
}
