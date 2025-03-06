package org.swiggy.order.DTO;


import lombok.Data;

@Data
public class AssignDEResponseDTO {

    private Long orderId;
    private String status;
    private Long deId;
    private String error;
}