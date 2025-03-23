package org.swiggy.order.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiggy.order.DTO.MenuItem;
import org.swiggy.order.Enum.OrderStatus;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Model.User;
import org.swiggy.order.Service.Factory.OrderFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderFactoryTest {

    @InjectMocks
    private OrderFactory orderFactory;

    private User user;
    private List<MenuItem> menuItems;
    private Map<Long, Money> menuItemPrices;
    private Long restaurantId;

    @BeforeEach
    void setUp() {
        orderFactory = new OrderFactory();
        user = new User();
        restaurantId = 1L;

        MenuItem item1 = new MenuItem(1L, 2, "Burger");
        MenuItem item2 = new MenuItem(2L, 3, "Pizza");
        menuItems = Arrays.asList(item1, item2);

        menuItemPrices = Map.of(
                1L, new Money(100, Currency.getInstance("INR")),
                2L, new Money(200, Currency.getInstance("INR"))
        );
    }

    @Test
    public void testCreateOrder() {
        Order order = orderFactory.createOrder(restaurantId, user, menuItems,menuItemPrices);

        assertNotNull(order);
        assertEquals(restaurantId, order.getRestaurantId());
        assertEquals(user, order.getUser());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    public void testCreateOrderItems() {
        Order order = orderFactory.createOrder(restaurantId, user, menuItems,menuItemPrices);


        // Using reflection to access private method for testing
        List<OrderItem> orderItems = order.getOrderItems();

        assertEquals(2, orderItems.size());

        // Verify first item
        OrderItem firstItem = orderItems.get(0);
        assertEquals(order, firstItem.getOrder());
        assertEquals(new Money(100, Currency.getInstance("INR")), firstItem.getPrice());
        assertEquals(2, firstItem.getQuantity());
        assertEquals("Burger", firstItem.getName());

        // Verify second item
        OrderItem secondItem = orderItems.get(1);
        assertEquals(order, secondItem.getOrder());
        assertEquals(new Money(200, Currency.getInstance("INR")), secondItem.getPrice());
        assertEquals(3, secondItem.getQuantity());
        assertEquals("Pizza", secondItem.getName());
    }
    @Test
    public void testCalculateTotalAmount() {
        // Create specific test data with known prices for predictable calculation
        MenuItem burger = new MenuItem(1L, 2, "Burger");
        MenuItem pizza = new MenuItem(2L, 3, "Pizza");
        MenuItem drink = new MenuItem(3L, 4, "Drink");
        List<MenuItem> testMenuItems = Arrays.asList(burger, pizza, drink);

        Map<Long, Money> testPrices = Map.of(
                1L, new Money(100, Currency.getInstance("INR")),
                2L, new Money(200, Currency.getInstance("INR")),
                3L, new Money(50, Currency.getInstance("INR"))
        );

        // Create a complete order using the factory
        Order order = orderFactory.createOrder(restaurantId, user, testMenuItems, testPrices);

        // Verify the total amount calculation
        Money totalAmount = order.getTotalAmount();

        // Expected calculation: (100 * 2) + (200 * 3) + (50 * 4) = 200 + 600 + 200 = 1000
        assertEquals(1000.0, totalAmount.getAmount());
        assertEquals(Currency.getInstance("INR"), totalAmount.getCurrency());
    }

    @Test
    public void testCreateCompleteOrder() {
        Order order = orderFactory.createOrder(restaurantId, user, menuItems, menuItemPrices);

        // Verify order properties
        assertNotNull(order);
        assertEquals(restaurantId, order.getRestaurantId());
        assertEquals(user, order.getUser());

        // Verify order items
        List<OrderItem> orderItems = order.getOrderItems();
        assertEquals(2, orderItems.size());

        // Verify total amount calculation
        Money totalAmount = order.getTotalAmount();
        // Expected: (100 * 2) + (200 * 3) = 200 + 600 = 800
        assertEquals(800.0, totalAmount.getAmount());
        assertEquals(Currency.getInstance("INR"), totalAmount.getCurrency());
    }

    @Test
    public void testCreateCompleteOrderWithEmptyItems() {
        List<MenuItem> emptyItems = Collections.emptyList();

        Order order = orderFactory.createOrder(restaurantId, user, emptyItems, menuItemPrices);

        // Verify order properties
        assertNotNull(order);
        assertEquals(restaurantId, order.getRestaurantId());

        // Verify order items
        List<OrderItem> orderItems = order.getOrderItems();
        assertTrue(orderItems.isEmpty());

        // Verify total amount is zero
        Money totalAmount = order.getTotalAmount();
        assertEquals(0.0, totalAmount.getAmount());
    }

    @Test
    public void testCreateOrderItemsWithInvalidMenuItemId() {
        // Create a menu item with ID that doesn't exist in prices map
        MenuItem invalidItem = new MenuItem(999L, 1, "Invalid Item");
        List<MenuItem> itemsWithInvalid = new ArrayList<>(menuItems);
        itemsWithInvalid.add(invalidItem);

        // Expect NullPointerException when trying to get price for invalid item ID
        // Testing directly on createCompleteOrder instead of createOrderItems
        assertThrows(NullPointerException.class, () -> {
            orderFactory.createOrder(restaurantId, user, itemsWithInvalid, menuItemPrices);
        });
    }
    @Test
    void testCreateCompleteOrder_WithZeroQuantityItems() {
        // Arrange - Create a mix of regular and zero-quantity items
        MenuItem regularItem = new MenuItem(1L, 2, "Regular Item");
        MenuItem zeroItem = new MenuItem(2L, 0, "Zero Quantity Item");
        List<MenuItem> mixedItems = Arrays.asList(regularItem, zeroItem);

        Map<Long, Money> prices = Map.of(
                1L, new Money(100, Currency.getInstance("INR")),
                2L, new Money(50, Currency.getInstance("INR"))
        );

        // Act - Create order with the mixed items
        Order order = orderFactory.createOrder(restaurantId, user, mixedItems, prices);

        // Assert
        List<OrderItem> orderItems = order.getOrderItems();

        // Should have both items
        assertEquals(2, orderItems.size(), "Order should contain both items regardless of quantity");

        // Find the zero quantity item
        OrderItem zeroQuantityOrderItem = orderItems.stream()
                .filter(item -> item.getName().equals("Zero Quantity Item"))
                .findFirst()
                .orElse(null);

        // Verify zero quantity item properties
        assertNotNull(zeroQuantityOrderItem, "Zero quantity item should be included in order");
        assertEquals(0, zeroQuantityOrderItem.getQuantity());
        assertEquals(50.0, zeroQuantityOrderItem.getPrice().getAmount());

        // Check total calculation - should only include the regular item
        Money totalAmount = order.getTotalAmount();
        assertEquals(200.0, totalAmount.getAmount(), "Total should only include non-zero quantity items");
    }



}
