package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.AssignDERequestDTO;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.DTO.RestaurantDTO;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.External.FulfillmentServiceClient;

import java.util.List;

@Service
public class FulfillmentService {

    private final FulfillmentServiceClient fulfillmentServiceClient;
    private final CatalogServiceClient catalogServiceClient;

    public FulfillmentService(FulfillmentServiceClient fulfillmentServiceClient, CatalogServiceClient catalogServiceClient) {
        this.fulfillmentServiceClient = fulfillmentServiceClient;
        this.catalogServiceClient = catalogServiceClient;
    }

    public void assignDeliveryExecutive(Order order, List<MenuItemDTO> orderItems) {
        RestaurantDTO restaurantDTO = catalogServiceClient.fetchRestaurantInfo(order.getRestaurantId());
        AssignDERequestDTO assignDERequestDTO = new AssignDERequestDTO(order, restaurantDTO, orderItems);

        try {
            fulfillmentServiceClient.assignDeliveryExecutive(assignDERequestDTO);
        } catch (Exception e) {
            System.err.println("Failed to assign Delivery Executive: " + e.getMessage());
            // TODO: Implement retry mechanism if necessary
        }
    }




}
