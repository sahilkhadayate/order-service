package org.swiggy.order.Service.External;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.swiggy.order.DTO.AssignDERequest;
import org.swiggy.order.DTO.AssignDEResponse;

@Service
public class FulfillmentServiceClient {


    private final RestTemplate restTemplate;
    private final String fulfillmentServiceUrl;

    public FulfillmentServiceClient(RestTemplate restTemplate, @Value("${fulfillment.service.url}") String fulfillmentServiceUrl) {
        this.restTemplate = restTemplate;
        this.fulfillmentServiceUrl = fulfillmentServiceUrl;
    }

    public AssignDEResponse assignDeliveryExecutive(AssignDERequest assignDERequest) {
        HttpEntity<AssignDERequest> requestEntity = new HttpEntity<>(assignDERequest, new HttpHeaders());
        // Deserialize the raw JSON response into AssignDEResponseDTO
        AssignDEResponse response = restTemplate.postForObject(
                fulfillmentServiceUrl + "/v1/delivery",
                requestEntity,
                AssignDEResponse.class
        );

        System.out.println("Deserialized response: {} "+ response);
        return response;

    }

}

