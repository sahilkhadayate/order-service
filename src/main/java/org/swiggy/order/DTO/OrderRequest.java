package org.swiggy.order.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class OrderRequest {

    @Positive(message = "Restaurant id is required")
    private Long restaurantId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<MenuItem> orderItems;

    public OrderRequest(Long restaurantId, List<MenuItem> items) {
        this.restaurantId = restaurantId;
        this.orderItems = items;
    }
}
