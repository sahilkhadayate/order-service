package org.swiggy.order.Service.Factory;

import org.springframework.stereotype.Component;
import org.swiggy.order.DTO.OrderRequestDTO;
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
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderFactory {

    private final CatalogServiceClient catalogServiceClient;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    public OrderFactory(CatalogServiceClient catalogServiceClient, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.catalogServiceClient = catalogServiceClient;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order createOrder(OrderRequestDTO orderRequestDTO, User user) throws ResourceDoesNotExistException {
        Map<Long, Money> menuItemPrices = catalogServiceClient.fetchPricesForMenuItems(orderRequestDTO.getRestaurantId());
        if (menuItemPrices.isEmpty()) {
            throw new ResourceDoesNotExistException("No menu items found for restaurant");
        }

        Order order = new Order(orderRequestDTO.getRestaurantId(), user);
        order = orderRepository.save(order);

        List<OrderItem> orderItems = createOrderItems(orderRequestDTO, menuItemPrices, order);
        order.setTotalAmount(calculateTotalAmount(orderItems));
        orderRepository.save(order);

        return order;
    }

    private List<OrderItem> createOrderItems(OrderRequestDTO orderRequestDTO, Map<Long, Money> menuItemPrices, Order order) {
        List<OrderItem> orderItems = orderRequestDTO.getOrderItems().stream()
                .map(item -> new OrderItem(item, order, menuItemPrices.get(item.getId())))
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }

    private Money calculateTotalAmount(List<OrderItem> orderItems) {
        Money totalAmount = new Money(0.0, Currency.getInstance("INR")); // Assuming INR as default
        for (OrderItem orderItem : orderItems) {
            double val = orderItem.getPrice().getAmount() * orderItem.getQuantity();
            totalAmount = totalAmount.add(new Money(val, orderItem.getPrice().getCurrency()));
        }
        return totalAmount;
    }

    public Order changeOrderStatus(Long restaurantId, Long orderId) throws ResourceDoesNotExistException {
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId);
        if (order == null) {
            throw new ResourceDoesNotExistException("Order not found");
        }
        if (order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new OrderAlreadyDeliveredException("Order already delivered");
        }
        order.fulfillOrder();
        orderRepository.save(order);
        return order;
    }
}
