package org.swiggy.order.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class OrderRequestDTO {


    @Positive(message = "Restaurant id is required")
    private Long restaurantId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<MenuItemDTO> orderItems;

    public OrderRequestDTO(Long restaurantId, List<MenuItemDTO> items) {
        this.restaurantId = restaurantId;
        this.orderItems = items;
    }
}
