package org.swiggy.order.Service.External;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;
import org.swiggy.order.DTO.RestaurantDTO;
import org.swiggy.order.Model.Money;

import java.util.Map;

@Service
public class CatalogServiceClient {

    private final RestClient restClient;

    public CatalogServiceClient(RestClient.Builder restClientBuilder, @Value("${catalog.service.url}") String catalogServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(catalogServiceUrl).build();
    }

    public Map<Long, Money> fetchPricesForMenuItems(Long restaurantId) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/restaurants/{restaurantId}/menu-items")
                        .build(restaurantId))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<Long, Money>>() {});
    }


    public RestaurantDTO fetchRestaurantInfo(@Positive(message = "Restaurant id is required") Long restaurantId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/restaurants/{restaurantId}")
                        .build(restaurantId))
                .retrieve()
                .body(RestaurantDTO.class);

    }
}



