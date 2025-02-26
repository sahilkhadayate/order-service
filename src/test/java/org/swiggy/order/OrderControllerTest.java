package org.swiggy.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.DTO.OrderRequestDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private OrderRequestDTO orderRequestDTO;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        List<MenuItemDTO> items = new ArrayList<>();
        orderRequestDTO = new OrderRequestDTO(1L, items);
    }

    @Test
    void testOrderCreationThrowsBadRequestForEmptyItems() throws Exception {

        mockMvc.perform(post("/users/1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDTO))).andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("{\"orderItems\":\"Order must contain at least one item\"}", result.getResponse().getContentAsString()));
    }

    @Test
    void testOrderCreationThrowsBadRequestForInvalidRestaurantId() throws Exception {
        List<MenuItemDTO> items = new ArrayList<>();
        items.add( new MenuItemDTO(1L,3));

        orderRequestDTO = new OrderRequestDTO(-3L, items);

        mockMvc.perform(post("/users/1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO))).andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("{\"restaurantId\":\"Restaurant id is required\"}", result.getResponse().getContentAsString()));
    }


}
