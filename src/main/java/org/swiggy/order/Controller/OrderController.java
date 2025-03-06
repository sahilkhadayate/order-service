package org.swiggy.order.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swiggy.order.DTO.OrderRequestDTO;
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
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO, @PathVariable String userId) throws ResourceDoesNotExistException {
        System.out.println("====================================================");
        System.out.println("Creating order");
        System.out.println("====================================================");

        orderService.createOrder(orderRequestDTO, Long.parseLong(userId));
        return ResponseEntity.ok().body("Order created successfully");
    }


}
