package org.swiggy.order.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swiggy.order.DTO.AssignDEResponse;
import org.swiggy.order.DTO.CreateOrderResponse;
import org.swiggy.order.DTO.OrderRequest;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Service.OrderService;

@RestController
@RequestMapping("/users/{userId}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest, @PathVariable Long userId) throws ResourceDoesNotExistException {

        AssignDEResponse assignDEResponse = orderService.createOrder(orderRequest, userId);
        CreateOrderResponse createOrderResponse = new CreateOrderResponse(assignDEResponse);
        return ResponseEntity.ok().body(createOrderResponse);
    }


}
