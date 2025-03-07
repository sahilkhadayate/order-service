package org.swiggy.order.Service.External;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.swiggy.order.DTO.AssignDERequestDTO;
import org.swiggy.order.DTO.AssignDEResponseDTO;

@Service
public class FulfillmentServiceClient {


    private final RestTemplate restTemplate;
    private final String fulfillmentServiceUrl;

    public FulfillmentServiceClient(RestTemplate restTemplate, @Value("${fulfillment.service.url}") String fulfillmentServiceUrl) {
        this.restTemplate = restTemplate;
        this.fulfillmentServiceUrl = fulfillmentServiceUrl;
    }

    public AssignDEResponseDTO assignDeliveryExecutive(AssignDERequestDTO assignDERequestDTO) {
        return restTemplate.postForObject(fulfillmentServiceUrl + "/v1/delivery", assignDERequestDTO, AssignDEResponseDTO.class);

    }

}

