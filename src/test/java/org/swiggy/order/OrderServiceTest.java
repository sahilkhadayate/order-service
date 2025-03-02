package org.swiggy.order;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Exception.InvalidUserException;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Repository.OrderItemRepository;
import org.swiggy.order.Repository.OrderRepository;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.OrderService;
import org.swiggy.order.Service.UserService.UserService;

import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {


    @MockitoBean
    private UserService userService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private CatalogServiceClient catalogServiceClient;


    private OrderRequestDTO orderRequestDTO;

    @Test
    public void testCreateOrderThrowsInvalidUserException() {

        when(userService.authenticateUser("nonExistentUser", 1L)).thenThrow(new InvalidUserException("Invalid User"));

        assertThrows(InvalidUserException.class, () -> orderService.createOrder(orderRequestDTO));

    }

    @Test
    public void testCreateOrderThrowsAccessDeniedException() {

        when(userService.authenticateUser("nonExistentUser", 1L)).thenThrow(new AccessDeniedException("access denied"));

        assertThrows(AccessDeniedException.class, () -> orderService.createOrder(orderRequestDTO));

    }

    @Test
    public void testFetchPricesOfRestaurantMenu() {
        when(catalogServiceClient.getMenuItemPrices(1L)).thenReturn(null);
    }


    @Test
    void createOrder_ShouldThrowException_WhenMenuItemsNotFound() {
        // Arrange
        OrderRequestDTO requestDTO = new OrderRequestDTO(1L, List.of(new MenuItemDTO(101L, 2)));

        when(catalogServiceClient.getMenuItemPrices(1L)).thenReturn(Map.of());

        // Act & Assert
        ResourceDoesNotExistException exception = assertThrows(ResourceDoesNotExistException.class,
                () -> orderService.createOrder(requestDTO));

        assertEquals("No menu items found for restaurant", exception.getMessage());

        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldSaveOrderAndOrderItems_WhenMenuItemsExist() throws ResourceDoesNotExistException {
        // Arrange
        OrderRequestDTO requestDTO = new OrderRequestDTO(1L, List.of(
                new MenuItemDTO(101L, 2),
                new MenuItemDTO(102L, 1)
        ));

        Map<Long, Money> menuItemPrices = Map.of(
                101L, new Money(10.0, Currency.getInstance("INR")),
                102L, new Money(5.0, Currency.getInstance("INR"))
        );

        when(catalogServiceClient.getMenuItemPrices(1L)).thenReturn(menuItemPrices);

        // Act
        Order order = orderService.createOrder(requestDTO);

        // Assert: Capture order and order items
        verify(orderRepository).save(order);
        assertEquals(1L, order.getRestaurantId());

        ArgumentCaptor<OrderItem> orderItemCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository, times(2)).save(orderItemCaptor.capture());
        List<OrderItem> savedOrderItems = orderItemCaptor.getAllValues();


        assertEquals(2, savedOrderItems.size());
        assertEquals(10, savedOrderItems.get(0).getPrice().getAmount());
        assertEquals(5, savedOrderItems.get(1).getPrice().getAmount());
    }

}


