package org.swiggy.order.Service.Factory;

import org.springframework.stereotype.Component;
import org.swiggy.order.DTO.MenuItem;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Model.User;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderFactory {



    public Order createOrder(Long restaurantId , User user, List<MenuItem> menuItems, Map<Long, Money> menuItemPrices) {
        // Create base order

        Order order = new Order(restaurantId, user);

        // Create order items connected to this order
        List<OrderItem> orderItems = createOrderItems(menuItems, menuItemPrices, order);
order.setOrderItems(orderItems);
        // Calculate and set total amount
        Money totalAmount = calculateTotalAmount(orderItems);
        order.setTotalAmount(totalAmount);

        return order;
    }

    private List<OrderItem> createOrderItems(List<MenuItem> menuItems, Map<Long, Money> menuItemPrices, Order order) {
        return menuItems.stream()
                .map(item -> new OrderItem(item, order, menuItemPrices.get(item.getId())))
                .collect(Collectors.toList());
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
