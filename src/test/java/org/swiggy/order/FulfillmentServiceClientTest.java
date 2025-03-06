package org.swiggy.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.swiggy.order.DTO.AssignDERequestDTO;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.DTO.RestaurantDTO;
import org.swiggy.order.Service.External.FulfillmentServiceClient;

import java.util.Arrays;

public class FulfillmentServiceClientTest {


    private FulfillmentServiceClient fulfillmentServiceClient;

   // private MockRestServiceServer mockServer;


    private final String fulfillmentServiceUrl = "http://localhost:8090";

    @BeforeEach
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        this.fulfillmentServiceClient = new FulfillmentServiceClient(restTemplate, fulfillmentServiceUrl);
    //    this.mockServer = MockRestServiceServer.createServer(restTemplate);
    }
//
//    @Test
//    public void testAssignDeliveryExecutive() {
//        AssignDERequestDTO requestDTO = new AssignDERequestDTO();
//        requestDTO.setOrderId(1L);
//        requestDTO.setCustomerId(1L);
//        requestDTO.setRestaurant(new RestaurantDTO(1L, "Restaurant Name", "Location"));
//        requestDTO.setItems(Arrays.asList(new ItemDTO("Item1", 2), new ItemDTO("Item2", 3)));
//        requestDTO.setTotal(100L);
//
//        mockServer.expect(requestTo(fulfillmentServiceUrl + "/v1/delivery"))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json("{\"orderId\":1,\"customerId\":1,\"restaurant\":{\"restaurantId\":1,\"name\":\"Restaurant Name\",\"location\":\"Location\"},\"items\":[{\"name\":\"Item1\",\"quantity\":2},{\"name\":\"Item2\",\"quantity\":3}],\"total\":100}"))
//                .andRespond(withSuccess("{\"orderId\":1}", MediaType.APPLICATION_JSON));
//
//        fulfillmentServiceClient.assignDeliveryExecutive(requestDTO);
//
//        mockServer.verify();
//    }

    @Test
    public void testAssignDeliveryExecutive_ActualCallToService() {
        AssignDERequestDTO requestDTO = new AssignDERequestDTO();
        requestDTO.setOrderId(2L);
        requestDTO.setCustomerId(1L);
        requestDTO.setRestaurant(new RestaurantDTO(1L, "Restaurant Name", "loc1"));
        requestDTO.setItems(Arrays.asList(new MenuItemDTO(2L,2,"Item1"), new MenuItemDTO(1L,3,"Item2" )));
        requestDTO.setTotal(100L);

        fulfillmentServiceClient.assignDeliveryExecutive(requestDTO);
    }
}
