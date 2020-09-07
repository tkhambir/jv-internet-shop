package com.internet.shop.dao;

import com.internet.shop.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Order create(Order order);

    List<Order> getUserOrders(Long userId);

    Optional<Order> get(Long id);

    List<Order> getAll();

    boolean delete(Long id);
}
