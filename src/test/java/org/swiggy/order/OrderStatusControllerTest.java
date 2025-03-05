package org.swiggy.order;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.swiggy.order.Config.SecurityConfig;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Repository.UserRepository;
import org.swiggy.order.Service.OrderService;
import org.swiggy.order.Service.UserService.UserService;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class OrderStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    public void testThrowsExceptionWhenRestaurantIdIsNotValid() throws ResourceDoesNotExistException, Exception {
        doThrow(new ResourceDoesNotExistException("Restaurant does not exist")).when(orderService).updateOrderStatus(1L, 1L);

        mockMvc.perform(put("/restaurants/1/orders/1/status")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }


}
