package org.swiggy.order.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swiggy.order.Model.Order;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Order findByIdAndRestaurantId(Long orderId, Long restaurantId);
}
