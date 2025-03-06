package org.swiggy.order.DTO;

import lombok.Data;

import java.util.List;

@Data
public class AssignDERequestDTO {

        private Long orderId;
        private Long customerId;
        private RestaurantDTO restaurant;
        private List<MenuItemDTO> items;
        private double total;

}



