package org.swiggy.order.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.swiggy.order.DTO.RestaurantRequest;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Service.External.CatalogServiceClient;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
public class CatalogServiceClientTest {


    private CatalogServiceClient catalogServiceClient;

    private MockRestServiceServer mockServer;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    @BeforeEach
    public void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder().baseUrl(catalogServiceUrl);
        this.catalogServiceClient = new CatalogServiceClient(restClientBuilder, catalogServiceUrl);
        this.mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
    }

    @Test
    public void testFetchPricesForMenuItems_InRealTime() {
        Long restaurantId = 2L;
        Map<Long, Money> expectedResponse = new HashMap<>();
        expectedResponse.put(5L, new Money(150.0, Currency.getInstance("INR")));

      //  mockServer.expect(requestTo(catalogServiceUrl + "/internal/restaurants/" + restaurantId + "/menu-items"))

        Map<Long, Money> response = catalogServiceClient.fetchPricesForMenuItems(restaurantId);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testFetchPricesForMenuItems() {
        Long restaurantId = 2L;
        Map<Long, Money> expectedResponse = new HashMap<>();
        expectedResponse.put(5L, new Money(150.0, Currency.getInstance("INR")));

        mockServer.expect(requestTo(catalogServiceUrl + "/internal/restaurants/" + restaurantId + "/menu-items"))
                .andRespond(withSuccess("{\"5\":{\"amount\":150.0,\"currency\":\"INR\"}}", MediaType.APPLICATION_JSON));

        Map<Long, Money> response = catalogServiceClient.fetchPricesForMenuItems(restaurantId);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testFetchRestaurantInfo() {
        Long restaurantId = 1L;
        RestaurantRequest expectedResponse = new RestaurantRequest(1L, "rest2", "city2");

//        mockServer.expect(requestTo(catalogServiceUrl + "/internal/restaurants/" + restaurantId))
//                .andRespond(withSuccess("{\"restaurantId\":1,\"name\":\"Restaurant Name\",\"location\":\"Location\"}", MediaType.APPLICATION_JSON));

        RestaurantRequest response = catalogServiceClient.fetchRestaurantInfo(restaurantId);

        assertEquals(expectedResponse, response);
    }
}
