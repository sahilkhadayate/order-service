package org.swiggy.order.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swiggy.order.DTO.OrderRequestDTO;

@RestController
@RequestMapping("/users/{userId}/orders")
public class OrderController {


    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO, @PathVariable String userId) {

        return ResponseEntity.ok().body("Order created successfully");
    }


}
