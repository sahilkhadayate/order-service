package org.swiggy.order.DTO;


import lombok.Data;

@Data
public class AssignDEResponse {

    private Long orderId;
    private String status;
    private Long deId;
}