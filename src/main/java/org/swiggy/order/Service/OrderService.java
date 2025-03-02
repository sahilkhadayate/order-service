package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.MenuItemDTO;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Repository.OrderItemRepository;
import org.swiggy.order.Repository.OrderRepository;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.UserService.UserService;
import java.util.Map;

@Service
public class OrderService {

    private final UserService userService;

    private final CatalogServiceClient catalogServiceClient;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    public OrderService(UserService userService, CatalogServiceClient catalogServiceClient, OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.userService = userService;
        this.catalogServiceClient = catalogServiceClient;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order createOrder(OrderRequestDTO orderRequestDTO) throws ResourceDoesNotExistException {
        Map<Long, Money> menuItemPrices = catalogServiceClient.getMenuItemPrices(orderRequestDTO.getRestaurantId());
        if (menuItemPrices.isEmpty()) {
            throw new ResourceDoesNotExistException("No menu items found for restaurant");
        }
        Money totalAmount = new Money();
        Order order = new Order(orderRequestDTO.getRestaurantId());
        for (MenuItemDTO item: orderRequestDTO.getOrderItems()) {
            OrderItem orderItem = new OrderItem(item, order);
            Money price = menuItemPrices.get(item.getId());
            orderItem.setPrice(price);
            orderItemRepository.save(orderItem);
            totalAmount.add(price);
        }
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
return order;
    }
}
