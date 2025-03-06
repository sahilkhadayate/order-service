package org.swiggy.order;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Enum.OrderStatus;
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

import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {


    @Mock
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private CatalogServiceClient catalogServiceClient;


    private OrderRequestDTO orderRequestDTO;

    @Test
    public void testCreateOrderThrowsInvalidUserException() {

        when(userService.authenticateUser("nonExistentUser", 1L)).thenThrow(new InvalidUserException("Invalid User"));

        assertThrows(InvalidUserException.class, () -> orderService.createOrder(orderRequestDTO, 1L));

    }

    @Test
    public void test_CreateOrderThrowsAccessDeniedException() {

        when(userService.authenticateUser("nonExistentUser", 1L)).thenThrow(new AccessDeniedException("access denied"));

        assertThrows(AccessDeniedException.class, () -> orderService.createOrder(orderRequestDTO,1L));

    }


    @Test
    void test_shouldThrowExceptionIfMenuItemsNotFound() {
        // Arrange
        Long restaurantId = 3L;
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(restaurantId, List.of());

        when(catalogServiceClient.getMenuItemPrices(restaurantId)).thenReturn(Collections.emptyMap());

        // Act & Assert
        assertThrows(ResourceDoesNotExistException.class,
                () -> orderService.createOrder(orderRequestDTO, 1L));
    }


    @Test
    void test_CreateOrder_ShouldThrowException_WhenMenuItemsNotFound() {
        // Arrange
        OrderRequestDTO requestDTO = new OrderRequestDTO(1L, List.of(new MenuItemDTO(101L, 2,"name3")));

        when(catalogServiceClient.getMenuItemPrices(1L)).thenReturn(Map.of());

        // Act & Assert
        ResourceDoesNotExistException exception = assertThrows(ResourceDoesNotExistException.class,
                () -> orderService.createOrder(requestDTO,1L));

        assertEquals("No menu items found for restaurant", exception.getMessage());

        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void test_createOrder_ShouldSaveOrderAndOrderItems_WhenMenuItemsExist() throws ResourceDoesNotExistException {
        // Arrange
        OrderRequestDTO requestDTO = new OrderRequestDTO(1L, List.of(
                new MenuItemDTO(101L, 2,"name1"),
                new MenuItemDTO(102L, 1,"name2")
        ));

        Map<Long, Money> menuItemPrices = Map.of(
                101L, new Money(10.0, Currency.getInstance("INR")),
                102L, new Money(5.0, Currency.getInstance("INR"))
        );

        when(catalogServiceClient.getMenuItemPrices(1L)).thenReturn(menuItemPrices);
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        Order order = orderService.createOrder(requestDTO,1L);

        // Assert: Capture order and order items
        verify(orderRepository, times(2)).save(order);
        assertEquals(1L, order.getRestaurantId());

        ArgumentCaptor<List<OrderItem>> orderItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepository, times(1)).saveAll(orderItemsCaptor.capture());

        List<OrderItem> savedOrderItems = orderItemsCaptor.getValue();

        assertEquals(2, savedOrderItems.size());

        assertEquals(new Money(10, Currency.getInstance("INR")), savedOrderItems.get(0).getPrice());
        assertEquals(new Money(5, Currency.getInstance("INR")), savedOrderItems.get(1).getPrice());

    }



    @Test
    public void test_UpdateOrderStatusThrowsExceptionWhenTheRestaurantIdDoesNotExist() {

        when(orderRepository.findByIdAndRestaurantId(1L, 1L)).thenReturn(null);
        assertThrows(ResourceDoesNotExistException.class,()->orderService.updateOrderStatus(1L, 1L));
        verify(orderRepository, times(1)).findByIdAndRestaurantId(1L, 1L);
    }

    @Test
    public void testUpdateOrderStatus_ChangesOrderStatus() throws ResourceDoesNotExistException {
        // Arrange
        Long restaurantId = 1L;
        Long orderId = 1L;
        Order order = new Order(restaurantId);
        order.setId(orderId);

        when(orderRepository.findByIdAndRestaurantId(orderId, restaurantId)).thenReturn(order);

        // Act
        orderService.updateOrderStatus(restaurantId, orderId);

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

}


