package org.swiggy.order.DTO;

import lombok.Data;
import org.swiggy.order.Model.Order;

import java.util.List;

@Data
public class AssignDERequest {

    private Long orderId;
    private RestaurantRequest restaurant;
    private Long customerId;
    private List<MenuItem> items;
    private MoneyRequest total;


    public AssignDERequest(Order finalOrder, RestaurantRequest restaurantRequest, List<MenuItem> items) {
        this.orderId = finalOrder.getId();
        this.customerId = finalOrder.getUser().getId();
        this.restaurant = restaurantRequest;
        this.items = items;
        this.total = new MoneyRequest(finalOrder.getTotalAmount());
    }

    public AssignDERequest() {
    }
}



