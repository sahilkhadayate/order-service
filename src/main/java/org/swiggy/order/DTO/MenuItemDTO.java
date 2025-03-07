package org.swiggy.order.DTO;

import lombok.Data;
import org.swiggy.order.Model.OrderItem;


@Data
public class MenuItemDTO {

    private Long id;
    private String name;
    private int quantity;

    public MenuItemDTO(Long id, int quantity, String name) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;

    }

    public MenuItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.quantity = orderItem.getQuantity();
        this.name = orderItem.getName();
    }
}
