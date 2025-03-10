package org.swiggy.order.Service.Factory;

import org.springframework.stereotype.Component;
import org.swiggy.order.DTO.OrderRequest;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderFactory {



    public Order createOrder(Long restaurantId , User user) {

        Order order = new Order(restaurantId, user);
        return order;
    }

    public List<OrderItem> createOrderItems(OrderRequest orderRequest, Map<Long, Money> menuItemPrices, Order order) {
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(item -> new OrderItem(item, order, menuItemPrices.get(item.getId())))
                .collect(Collectors.toList());

        return orderItems;
    }


}
