package org.swiggy.order;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Exception.InvalidUserException;
import org.swiggy.order.Service.OrderService;
import org.swiggy.order.Service.UserService.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {


    @MockitoBean
    private UserService userService;

    @Autowired
    private OrderService orderService;

    private OrderRequestDTO orderRequestDTO;

    @Test
    public void testCreateOrderThrowsInvalidUserException() {

        when(userService.authenticateUser("nonExistentUser", 1L)).thenThrow(new InvalidUserException("Invalid User"));

        assertThrows(InvalidUserException.class, () -> orderService.createOrder(orderRequestDTO, 1L, "nonExistentUser"));

    }

}

