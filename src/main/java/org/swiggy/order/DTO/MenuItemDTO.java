package org.swiggy.order.DTO;

import lombok.Data;


@Data
public class MenuItemDTO {

    private Long id;

    private int quantity;

    public MenuItemDTO(Long id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }
}
