package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.Enum.OrderStatus;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Repository.OrderItemRepository;
import org.swiggy.order.Repository.OrderRepository;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.UserService.UserService;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

//    public Order createOrder(OrderRequestDTO orderRequestDTO) throws ResourceDoesNotExistException {
//        Map<Long, Money> menuItemPrices = catalogServiceClient.getMenuItemPrices(orderRequestDTO.getRestaurantId());
//        if (menuItemPrices.isEmpty()) {
//            throw new ResourceDoesNotExistException("No menu items found for restaurant");
//        }
//        // Step 1: Save order without totalAmount (null)
//        Order order = new Order(orderRequestDTO.getRestaurantId());
//        order = orderRepository.save(order); // Now order has an ID
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        Money totalAmount = new Money(0.0, Currency.getInstance("INR")); // Assuming INR as default
//
//        for (MenuItemDTO item : orderRequestDTO.getOrderItems()) {
//            Money itemPrice = menuItemPrices.get(item.getId());
//            if (itemPrice == null) {
//                throw new IllegalArgumentException("Menu item price not found for item ID: " + item.getId());
//            }
//
//            OrderItem orderItem = new OrderItem(item, order);
//            orderItem.setPrice(itemPrice);
//            orderItems.add(orderItem);
//
//            double val = itemPrice.getAmount() * item.getQuantity();
//            totalAmount = totalAmount.add(new Money(val, itemPrice.getCurrency()));
//        }
//
//        orderItemRepository.saveAll(orderItems); // Save all order items at once
//
//        order.setTotalAmount(totalAmount);
//        orderRepository.save(order);
//        return order;
//    }

    public Order createOrder(OrderRequestDTO orderRequestDTO, Long userId) throws ResourceDoesNotExistException {

        Map<Long, Money> menuItemPrices = catalogServiceClient.getMenuItemPrices(orderRequestDTO.getRestaurantId());

        if (menuItemPrices.isEmpty()) {
            throw new ResourceDoesNotExistException("No menu items found for restaurant");
        }

        Order order = new Order(orderRequestDTO.getRestaurantId());
        order = orderRepository.save(order);
        Order finalOrder = order;

        List<OrderItem> orderItems = createOrderItems(orderRequestDTO, menuItemPrices, order);

        orderItemRepository.saveAll(orderItems);

        // Set total amount and save order
        Money totalAmount = calculateTotalAmount(orderItems);
        order.setTotalAmount(totalAmount);
        orderRepository.save(finalOrder);

        return finalOrder;
    }


    private List<OrderItem> createOrderItems(OrderRequestDTO orderRequestDTO, Map<Long, Money> menuItemPrices, Order order) {
        return orderRequestDTO.getOrderItems().stream()
                .map(item -> {
                    Money itemPrice = menuItemPrices.get(item.getId());
                    if (itemPrice == null) {
                        throw new IllegalArgumentException("Menu item price not found for item ID: " + item.getId());
                    }
                    return new OrderItem(item, order, itemPrice);
                })
                .collect(Collectors.toList());
    }


    private Money calculateTotalAmount(List<OrderItem> orderItems) {
        Money totalAmount = new Money(0.0, Currency.getInstance("INR")); // Assuming INR as default
        for (OrderItem orderItem : orderItems) {
            double val = orderItem.getPrice().getAmount() * orderItem.getQuantity();
            totalAmount = totalAmount.add(new Money(val, orderItem.getPrice().getCurrency()));
        }
        return totalAmount;
    }

    public void updateOrderStatus(Long restaurantId, Long orderId) throws ResourceDoesNotExistException {
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId);
        if (order == null) {
            throw new ResourceDoesNotExistException("Order not found");
        }
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }
}
