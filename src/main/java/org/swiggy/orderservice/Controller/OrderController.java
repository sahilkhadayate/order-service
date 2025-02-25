package org.swiggy.orderservice.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swiggy.orderservice.DTO.OrderRequestDTO;

@RestController
@RequestMapping("/users/{userId}/orders")
public class OrderController {

    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequestDTO, @PathVariable String userId) {

    }


}
