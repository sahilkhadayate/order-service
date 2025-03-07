package org.swiggy.order.DTO;

import lombok.Data;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;

import java.util.List;

@Data
public class AssignDERequestDTO {

        private Long orderId;
        private Long customerId;
        private RestaurantDTO restaurant;
        private List<MenuItemDTO> items;
        private Money total;


        public AssignDERequestDTO(Order finalOrder, RestaurantDTO restaurantDTO,List<MenuItemDTO> items) {
            this.orderId = finalOrder.getId();
            this.customerId = finalOrder.getUser().getId();
            this.restaurant = restaurantDTO;
            this.items = items;
            this.total = finalOrder.getTotalAmount();
        }

        public AssignDERequestDTO() {
        }
}



