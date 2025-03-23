package org.swiggy.order.ServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiggy.order.DTO.AssignDEResponse;
import org.swiggy.order.DTO.MenuItem;
import org.swiggy.order.DTO.OrderRequest;
import org.swiggy.order.Enum.OrderStatus;
import org.swiggy.order.Exception.OrderAlreadyDeliveredException;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Model.User;
import org.swiggy.order.Repository.OrderItemRepository;
import org.swiggy.order.Repository.OrderRepository;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.Factory.OrderFactory;
import org.swiggy.order.Service.FulfillmentService;
import org.swiggy.order.Service.OrderService;
import org.swiggy.order.Service.UserService.UserService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CatalogServiceClient catalogServiceClient;

    @Mock
    private OrderFactory orderFactory;

    @Mock
    private FulfillmentService fulfillmentService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrder_ShouldThrowException_WhenMenuItemsNotFound() {
        // Arrange
        Long userId = 1L;
        Long restaurantId = 3L;
        OrderRequest orderRequest = new OrderRequest(restaurantId, List.of(new MenuItem(101L, 2, "name3")));
        User user = new User();

        when(userService.getUser(userId)).thenReturn(user);
        when(catalogServiceClient.fetchPricesForMenuItems(restaurantId)).thenReturn(Collections.emptyMap());

        // Act & Assert
        ResourceDoesNotExistException exception = assertThrows(ResourceDoesNotExistException.class,
                () -> orderService.createOrder(orderRequest, userId));

        assertEquals("No menu items found for restaurant", exception.getMessage());

        // Verify no further interactions occurred
        verify(orderFactory, never()).createOrder(any(), any(), any(), any());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void testCreateOrder_ShouldCreateAndSaveOrder_WhenValidRequest() throws ResourceDoesNotExistException {
        // Arrange
        Long userId = 1L;
        Long restaurantId = 1L;
        List<MenuItem> menuItems = List.of(
                new MenuItem(101L, 2, "Burger"),
                new MenuItem(102L, 3, "Pizza")
        );
        OrderRequest orderRequest = new OrderRequest(restaurantId, menuItems);
        User user = new User();
        Order mockOrder = new Order(restaurantId, user);

        Map<Long, Money> menuItemPrices = Map.of(
                101L, new Money(100, Currency.getInstance("INR")),
                102L, new Money(200, Currency.getInstance("INR"))
        );

        // Add order items to the mock order
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(menuItems.get(0), mockOrder, menuItemPrices.get(101L)),
                new OrderItem(menuItems.get(1), mockOrder, menuItemPrices.get(102L))
        );

        AssignDEResponse expectedResponse = new AssignDEResponse();
        expectedResponse.setDeId(123L);

        // Setup mocks
        when(userService.getUser(userId)).thenReturn(user);
        when(catalogServiceClient.fetchPricesForMenuItems(restaurantId)).thenReturn(menuItemPrices);
        when(orderFactory.createOrder(eq(restaurantId), eq(user), eq(menuItems), eq(menuItemPrices)))
                .thenReturn(mockOrder);
        when(orderRepository.save(mockOrder)).thenReturn(mockOrder);
        when(fulfillmentService.assignDeliveryExecutive(mockOrder, menuItems)).thenReturn(expectedResponse);

        // Act
        AssignDEResponse response = orderService.createOrder(orderRequest, userId);

        // Assert
        assertNotNull(response);
        assertEquals(123L, response.getDeId());

        // Verify interactions
        verify(orderRepository).save(mockOrder);
        verify(orderItemRepository).saveAll(any());
        verify(fulfillmentService).assignDeliveryExecutive(mockOrder, menuItems);
    }

    @Test
    void testUpdateOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        Long restaurantId = 1L;
        Long orderId = 1L;

        when(orderRepository.findByIdAndRestaurantId(orderId, restaurantId)).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceDoesNotExistException.class,
                () -> orderService.updateOrderStatus(restaurantId, orderId));

        // Verify repository was called
        verify(orderRepository).findByIdAndRestaurantId(orderId, restaurantId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testUpdateOrderStatus_ShouldThrowException_WhenOrderAlreadyDelivered() {
        // Arrange
        Long restaurantId = 1L;
        Long orderId = 1L;
        Order order = new Order(restaurantId, new User());
        order.fulfillOrder(); // Set status to DELIVERED

        when(orderRepository.findByIdAndRestaurantId(orderId, restaurantId)).thenReturn(order);

        // Act & Assert
        assertThrows(OrderAlreadyDeliveredException.class,
                () -> orderService.updateOrderStatus(restaurantId, orderId));

        // Verify repository was called
        verify(orderRepository).findByIdAndRestaurantId(orderId, restaurantId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testUpdateOrderStatus_ShouldUpdateStatus_WhenOrderExists() throws ResourceDoesNotExistException {
        // Arrange
        Long restaurantId = 1L;
        Long orderId = 1L;
        Order order = new Order(restaurantId, new User());

        when(orderRepository.findByIdAndRestaurantId(orderId, restaurantId)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.updateOrderStatus(restaurantId, orderId);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getStatus());

        // Verify repository calls
        verify(orderRepository).findByIdAndRestaurantId(orderId, restaurantId);
        verify(orderRepository).save(order);
    }

    @Test
    void testCreateOrder_ShouldUseFactoryAndRepositories() throws ResourceDoesNotExistException {
        // Arrange
        Long userId = 1L;
        Long restaurantId = 1L;
        List<MenuItem> menuItems = List.of(new MenuItem(101L, 2, "Burger"));
        OrderRequest orderRequest = new OrderRequest(restaurantId, menuItems);
        User user = new User();

        Map<Long, Money> menuItemPrices = Map.of(
                101L, new Money(100, Currency.getInstance("INR"))
        );

        Order mockOrder = new Order(restaurantId, user);
        List<OrderItem> orderItems = Collections.singletonList(
                new OrderItem(menuItems.get(0), mockOrder, menuItemPrices.get(101L))
        );

        // Setup mocks
        when(userService.getUser(userId)).thenReturn(user);
        when(catalogServiceClient.fetchPricesForMenuItems(restaurantId)).thenReturn(menuItemPrices);
        when(orderFactory.createOrder(eq(restaurantId), eq(user), eq(menuItems), eq(menuItemPrices)))
                .thenReturn(mockOrder);
        when(orderRepository.save(mockOrder)).thenReturn(mockOrder);
        when(fulfillmentService.assignDeliveryExecutive(mockOrder, menuItems))
                .thenReturn(new AssignDEResponse());

        // Act
        orderService.createOrder(orderRequest, userId);

        // Assert - using ArgumentCaptor to capture and verify parameters
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();

        assertEquals(restaurantId, capturedOrder.getRestaurantId());
        assertEquals(user, capturedOrder.getUser());

        // Verify correct factory and service methods were called
        verify(orderFactory).createOrder(restaurantId, user, menuItems, menuItemPrices);
        verify(fulfillmentService).assignDeliveryExecutive(mockOrder, menuItems);
    }
}
