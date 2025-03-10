package org.swiggy.order.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiggy.order.DTO.MenuItem;
import org.swiggy.order.DTO.OrderRequest;
import org.swiggy.order.Enum.OrderStatus;
import org.swiggy.order.Exception.OrderAlreadyDeliveredException;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Money;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Model.OrderItem;
import org.swiggy.order.Model.User;
import org.swiggy.order.Service.Factory.OrderFactory;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderFactoryTest {


    @InjectMocks
    private OrderFactory orderFactory;


    private OrderRequest orderRequest;
    private Map<Long, Money> menuItemPrices;
    private Order order;


        @BeforeEach
        void setUp(){
            orderFactory = new OrderFactory();
            User user = new User();
            order = new Order(1L, user);

            MenuItem item1 = new MenuItem(1L, 2,"n1");
            MenuItem item2 = new MenuItem(2L, 3,"n2");
            orderRequest = new OrderRequest(1L,Arrays.asList(item1, item2));

            menuItemPrices = Map.of(
                    1L, new Money(100, Currency.getInstance("INR")),
                    2L, new Money(200,Currency.getInstance("INR"))
            );
        }

        @Test
        public void createOrderMethodReturnsOrderObject(){
            User user = new User();
            Order order = orderFactory.createOrder(1L,user);
            assertNotNull(order);
        }

    @Test
    public void testCreateOrderItems() {
        List<OrderItem> orderItems = orderFactory.createOrderItems(orderRequest, menuItemPrices, order);

        assertEquals(2, orderItems.size());
        assertEquals(order, orderItems.get(0).getOrder());
        assertEquals(order, orderItems.get(1).getOrder());
        assertEquals(new Money(100, Currency.getInstance("INR")), orderItems.get(0).getPrice());
        assertEquals(new Money(200,Currency.getInstance("INR")), orderItems.get(1).getPrice());
    }



}
