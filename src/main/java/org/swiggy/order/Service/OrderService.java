package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.User;
import org.swiggy.order.Service.Factory.OrderFactory;
import org.swiggy.order.Service.UserService.UserService;

@Service
public class OrderService {

    private final UserService userService;

    private final OrderFactory orderFactory;
    private final FulfillmentService fulfillmentService;

    public OrderService(UserService userService, OrderFactory orderFactory, FulfillmentService fulfillmentService) {
        this.userService = userService;
        this.orderFactory = orderFactory;
        this.fulfillmentService = fulfillmentService;
    }


    public Order createOrder(OrderRequestDTO orderRequestDTO, Long userId) throws ResourceDoesNotExistException {
        User user = userService.getUser(userId);
        Order order = orderFactory.createOrder(orderRequestDTO, user);
        fulfillmentService.assignDeliveryExecutive(order, orderRequestDTO.getOrderItems());
        return order;
    }


    public Order updateOrderStatus(Long restaurantId, Long orderId) throws ResourceDoesNotExistException {
        return orderFactory.changeOrderStatus(restaurantId, orderId);
    }
}
