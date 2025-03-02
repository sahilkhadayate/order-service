package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.UserService.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final UserService userService;

    private final CatalogServiceClient catalogServiceClient;

    public OrderService(UserService userService, CatalogServiceClient catalogServiceClient) {
        this.userService = userService;
        this.catalogServiceClient = catalogServiceClient;
    }

    public void createOrder(OrderRequestDTO orderRequestDTO) throws ResourceDoesNotExistException {
        List<OrderItem> orderItems = new ArrayList<>();
        Map<Long, Money> menuItemPrices = catalogServiceClient.getMenuItemPrices(orderRequestDTO.getRestaurantId());
        if (menuItemPrices.isEmpty()) {
            throw new ResourceDoesNotExistException("No menu items found for restaurant");
        }
        for (MenuItemDTO item: orderRequestDTO.getOrderItems()) {
            OrderItem orderItem = new OrderItem(item);
            Money price = menuItemPrices.get(item.getId());
            orderItem.setPrice(price);
            orderItems.add(orderItem);
        }
        Order order = new Order(orderRequestDTO.getRestaurantId(), orderItems);
        System.out.println("======================================================");
        for (Map.Entry<Long, Money> entry : menuItemPrices.entrySet()) {
            System.out.println("MenuItem ID: " + entry.getKey() + ", Price: " + entry.getValue());
        }
        System.out.println("======================================================");

    }
}
