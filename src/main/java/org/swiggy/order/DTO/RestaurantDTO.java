package org.swiggy.order.DTO;


import lombok.Data;

@Data
public class RestaurantDTO {
    private Long restaurantId;
    private String name;
    private String location;

    public RestaurantDTO(Long restaurantId, String restaurantName, String location) {
        this.restaurantId = restaurantId;
        this.name = restaurantName;
        this.location = location;
    }

    public RestaurantDTO() {
    }
    // Getters and Setters
}
