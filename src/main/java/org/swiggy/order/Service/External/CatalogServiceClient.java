package org.swiggy.order.Service.External;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.Model.Money;

import java.util.List;
import java.util.Map;

@Service
public class CatalogServiceClient {

    private final RestClient restClient;
    private final String catalogServiceUrl;

    public CatalogServiceClient(RestClient restClient, @Value("${catalog.service.url}") String catalogServiceUrl) {
        this.restClient = restClient;
        this.catalogServiceUrl = catalogServiceUrl;
    }

public Map<Long,Money> getMenuItemPrices(Long restaurantId) {
        return restClient.get().uri(catalogServiceUrl + "/internal/restaurants/" + restaurantId + "/menu-items")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}



