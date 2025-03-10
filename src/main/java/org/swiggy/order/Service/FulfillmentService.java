package org.swiggy.order.Service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.AssignDERequest;
import org.swiggy.order.DTO.AssignDEResponse;
import org.swiggy.order.DTO.MenuItem;
import org.swiggy.order.DTO.RestaurantRequest;
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

    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 2000)
    )


    public AssignDEResponse assignDeliveryExecutive(Order order, List<MenuItem> orderItems) {
        RestaurantRequest restaurantRequest = catalogServiceClient.fetchRestaurantInfo(order.getRestaurantId());
        AssignDERequest assignDERequest = new AssignDERequest(order, restaurantRequest, orderItems);
        return fulfillmentServiceClient.assignDeliveryExecutive(assignDERequest);

    }


}
