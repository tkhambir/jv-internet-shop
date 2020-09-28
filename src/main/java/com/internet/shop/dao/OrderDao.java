package com.internet.shop.dao;

import com.internet.shop.model.Order;
import com.internet.shop.model.Product;
import java.util.List;

public interface OrderDao extends GenericDao<Order, Long> {

    List<Order> getUserOrders(Long userId);

    Order addProduct(Order order, Product product);

    void deleteProduct(Order order, Product product);
}
