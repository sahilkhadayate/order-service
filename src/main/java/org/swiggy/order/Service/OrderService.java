package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.swiggy.order.DTO.AssignDERequestDTO;
import org.swiggy.order.DTO.OrderRequestDTO;
import org.swiggy.order.DTO.RestaurantDTO;
import org.swiggy.order.Enum.OrderStatus;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Model.User;
import org.swiggy.order.Repository.OrderItemRepository;
import org.swiggy.order.Repository.OrderRepository;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.External.FulfillmentServiceClient;
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

    private final FulfillmentServiceClient fulfillmentServiceClient;

    public OrderService(UserService userService, CatalogServiceClient catalogServiceClient, OrderItemRepository orderItemRepository, OrderRepository orderRepository, FulfillmentServiceClient fulfillmentServiceClient) {
        this.userService = userService;
        this.catalogServiceClient = catalogServiceClient;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.fulfillmentServiceClient = fulfillmentServiceClient;
    }



    public Order createOrder(OrderRequestDTO orderRequestDTO, Long userId) throws ResourceDoesNotExistException {

        Map<Long, Money> menuItemPrices = catalogServiceClient.fetchPricesForMenuItems(orderRequestDTO.getRestaurantId());
        if (menuItemPrices.isEmpty()) {
            throw new ResourceDoesNotExistException("No menu items found for restaurant");
        }

        User user = userService.getUser(userId);

        RestaurantDTO restaurantDTO = catalogServiceClient.fetchRestaurantInfo(orderRequestDTO.getRestaurantId());


        Order order = new Order(orderRequestDTO.getRestaurantId(), user);
        order = orderRepository.save(order);
        Order finalOrder = order;

        List<OrderItem> orderItems = createOrderItems(orderRequestDTO, menuItemPrices, order);
        Money totalAmount = calculateTotalAmount(orderItems);
        order.setTotalAmount(totalAmount);
        orderRepository.save(finalOrder);
        AssignDERequestDTO assignDERequestDTO = new AssignDERequestDTO(finalOrder, restaurantDTO, orderRequestDTO.getOrderItems());
        fulfillmentServiceClient.assignDeliveryExecutive(assignDERequestDTO);
        return finalOrder;
    }


    private List<OrderItem> createOrderItems(OrderRequestDTO orderRequestDTO, Map<Long, Money> menuItemPrices, Order order) {
        return orderRequestDTO.getOrderItems().stream()
                .map(item -> {
                    Money itemPrice = menuItemPrices.get(item.getId());
                    if (itemPrice == null) {
                        throw new IllegalArgumentException("Menu item price not found for item ID: " + item.getId());
                    }
                    OrderItem orderItem = new OrderItem(item, order, itemPrice);
                    orderItemRepository.save(orderItem);
                    return orderItem;
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
