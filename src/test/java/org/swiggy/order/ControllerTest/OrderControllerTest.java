package org.swiggy.order.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.swiggy.order.Config.SecurityConfig;
import org.swiggy.order.DTO.MenuItem;
import org.swiggy.order.DTO.OrderRequest;
import org.swiggy.order.Repository.UserRepository;
import org.swiggy.order.Service.UserService.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private OrderRequest orderRequest;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        List<MenuItem> items = new ArrayList<>();
        orderRequest = new OrderRequest(1L, items);
    }

    @Test
    void testOrderCreationThrowsBadRequestForEmptyItems() throws Exception {

        mockMvc.perform(post("/users/1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))).andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("{\"orderItems\":\"Order must contain at least one item\"}", result.getResponse().getContentAsString()));
    }

    @Test
    void testOrderCreationThrowsBadRequestForInvalidRestaurantId() throws Exception {
        List<MenuItem> items = new ArrayList<>();
        items.add( new MenuItem(1L,3,"name"));

        orderRequest = new OrderRequest(-3L, items);

        mockMvc.perform(post("/users/1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest))).andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("{\"restaurantId\":\"Restaurant id is required\"}", result.getResponse().getContentAsString()));
    }

    @Test
    void testOrderCreationSuccessWhenOrderIsCreatedSuccessfully() throws Exception {
        List<MenuItem> items = new ArrayList<>();
        items.add( new MenuItem(1L,3,"name"));

        orderRequest = new OrderRequest(3L, items);

        mockMvc.perform(post("/users/1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest))).andExpect(status().isOk());
    }

    @Test
    void testOrderCreationThrowsForbiddenWhenUserIsNotLoggedIn() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid Credentials"));

        List<MenuItem> items = new ArrayList<>();
        items.add( new MenuItem(1L,3,"name"));

        orderRequest = new OrderRequest(3L, items);

        mockMvc.perform(post("/users/1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest))).andExpect(status().isForbidden());
    }


}
