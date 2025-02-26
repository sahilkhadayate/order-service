package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Model.User;
import org.swiggy.order.Service.UserService.UserService;

@Service
public class OrderService {

    private final UserService userService;

    public OrderService(UserService userService) {
        this.userService = userService;
    }

    public void createOrder(OrderRequestDTO orderRequestDTO, Long userId, String username){
        User user = userService.authenticateUser(username,userId);

    }
}
