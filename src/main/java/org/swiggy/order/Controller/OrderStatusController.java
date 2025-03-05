package org.swiggy.order.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Service.OrderService;

@RestController
@RequestMapping("/restaurants/{restaurantId}/orders")
public class OrderStatusController {

    private final OrderService orderService;

    public OrderStatusController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String restaurantId, @PathVariable String orderId) throws ResourceDoesNotExistException {

        orderService.updateOrderStatus(Long.parseLong(restaurantId), Long.parseLong(orderId));
        return ResponseEntity.ok().body("Order status updated successfully");
    }

}
