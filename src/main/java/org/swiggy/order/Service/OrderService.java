package org.swiggy.order.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swiggy.order.DTO.AssignDEResponse;
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
import org.swiggy.order.Service.UserService.UserService;

import java.util.Currency;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final UserService userService;
    private final OrderFactory orderFactory;
    private final CatalogServiceClient catalogServiceClient;
    private final FulfillmentService fulfillmentService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(UserService userService, OrderFactory orderFactory, CatalogServiceClient catalogServiceClient, FulfillmentService fulfillmentService, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.userService = userService;
        this.orderFactory = orderFactory;
        this.catalogServiceClient = catalogServiceClient;
        this.fulfillmentService = fulfillmentService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }


    @Transactional
    public AssignDEResponse createOrder(OrderRequest orderRequest, Long userId) throws ResourceDoesNotExistException {
        User user = userService.getUser(userId);

        Map<Long, Money> menuItemPrices = catalogServiceClient.fetchPricesForMenuItems(orderRequest.getRestaurantId());
        if (menuItemPrices.isEmpty()) {
            throw new ResourceDoesNotExistException("No menu items found for restaurant");
        }

        Order order = orderFactory.createOrder(orderRequest.getRestaurantId(), user);
        order = orderRepository.save(order);

        List<OrderItem> orderItems = orderFactory.createOrderItems(orderRequest, menuItemPrices, order);
        orderItemRepository.saveAll(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));
        orderRepository.save(order);
        return fulfillmentService.assignDeliveryExecutive(order, orderRequest.getOrderItems());
    }

    public Order updateOrderStatus(Long restaurantId, Long orderId) throws ResourceDoesNotExistException {
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

    private Money calculateTotalAmount(List<OrderItem> orderItems) {
        Money totalAmount = new Money(0.0, Currency.getInstance("INR"));
        for (OrderItem orderItem : orderItems) {
            double val = orderItem.getPrice().getAmount() * orderItem.getQuantity();
            totalAmount = totalAmount.add(new Money(val, orderItem.getPrice().getCurrency()));
        }
        return totalAmount;
    }

}
