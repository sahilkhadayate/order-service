package org.swiggy.order.DTO;

import lombok.Data;

@Data
public class CreateOrderResponse {

    private AssignDEResponse assignDEResponse;

    private String message;


    public CreateOrderResponse(AssignDEResponse assignDEResponse) {
        this.assignDEResponse = assignDEResponse;
        this.message = "Order created successfully";
    }
}
