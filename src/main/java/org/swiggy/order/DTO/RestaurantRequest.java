package org.swiggy.order.DTO;


import lombok.Data;

@Data
public class RestaurantRequest {
    private Long restaurantId;
    private String name;
    private String location;

    public RestaurantRequest(Long restaurantId, String restaurantName, String location) {
        this.restaurantId = restaurantId;
        this.name = restaurantName;
        this.location = location;
    }

    public RestaurantRequest() {
    }
    // Getters and Setters
}
