package org.swiggy.order.DTO;

import lombok.Data;


@Data
public class MenuItemDTO {

    private String name;

    private Long id;

    private int quantity;

    public MenuItemDTO(Long id, int quantity, String name) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
    }
}
