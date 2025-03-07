package org.swiggy.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiggy.order.Enum.OrderStatus;
import org.swiggy.order.Exception.OrderAlreadyDeliveredException;
import org.swiggy.order.Exception.ResourceDoesNotExistException;
import org.swiggy.order.Model.Order;
import org.swiggy.order.Repository.OrderItemRepository;
import org.swiggy.order.Repository.OrderRepository;
import org.swiggy.order.Service.External.CatalogServiceClient;
import org.swiggy.order.Service.Factory.OrderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderFactoryTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CatalogServiceClient catalogServiceClient;
    @Mock
    private OrderItemRepository orderItemRepository;



    @InjectMocks
    private OrderFactory orderFactory;

        @BeforeEach
        void setUp(){
            orderFactory = new OrderFactory(catalogServiceClient,orderRepository,orderItemRepository);
        }
    @Test
    public void test_OrderFactoryCallsRepositoryToFetchOrderWhenUpdatingStatus() throws ResourceDoesNotExistException {

        Order order = new Order();
        when(orderRepository.findByIdAndRestaurantId(1L,12L)).thenReturn(order);

        orderFactory.changeOrderStatus(12L, 1L);
        
        verify(orderRepository).findByIdAndRestaurantId(1L,12L);
    }

    @Test
    public void test_OrderFactoryChangesStatusOfOrder() throws ResourceDoesNotExistException {

        Order order = new Order();
        when(orderRepository.findByIdAndRestaurantId(1L,12L)).thenReturn(order);

        orderFactory.changeOrderStatus(12L, 1L);

        verify(orderRepository).save(order);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    public void test_OrderFactoryUnableToChangeStatusOfOrderWhenAlreadyDelivered()  {

        Order order = new Order();
        when(orderRepository.findByIdAndRestaurantId(1L,12L)).thenReturn(order);
        order.fulfillOrder();

        assertThrows(OrderAlreadyDeliveredException.class,()->orderFactory.changeOrderStatus(12L, 1L));
    }

}
