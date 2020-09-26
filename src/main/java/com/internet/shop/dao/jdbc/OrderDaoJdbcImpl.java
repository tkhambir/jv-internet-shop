package com.internet.shop.dao.jdbc;

import com.internet.shop.dao.OrderDao;
import com.internet.shop.lib.Dao;
import com.internet.shop.lib.exceptions.DataProcessingException;
import com.internet.shop.model.Order;
import com.internet.shop.model.Product;
import com.internet.shop.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class OrderDaoJdbcImpl implements OrderDao {

    @Override
    public List<Order> getUserOrders(Long userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE fk_user_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(getOrder(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get orders for user with id " + userId, e);
        }
        for (Order order : orders) {
            order.setProducts(getProducts(order.getId()));
        }
        return orders;
    }

    @Override
    public Order create(Order order) {
        String query = "INSERT INTO orders(fk_user_id) VALUES (?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, order.getUserId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                order.setId(resultSet.getLong("GENERATED_KEY"));
            }
            return addProducts(order);
        } catch (SQLException e) {
            throw new DataProcessingException("Creating order"
                    + order + " is failed", e);
        }
    }

    @Override
    public Optional<Order> get(Long id) {
        String query = "SELECT o.id, o.fk_user_id, p.id, p.name, p.price FROM orders o "
                + "JOIN orders_products op on o.id = op.order_id "
                + "JOIN products p on p.id = op.product_id WHERE p.deleted = FALSE AND o.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            Order order = null;
            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                if (rs.isLast()) {
                    order = getOrder(rs);
                    order.setProducts(products);
                }

                products.add(new Product(rs.getLong("id"),
                        rs.getString("name"), rs.getDouble("price")));
            }

            return Optional.ofNullable(order);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get order with id " + id, e);
        }
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(getOrder(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get orders from DB", e);
        }
        for (Order order : orders) {
            order.setProducts(getProducts(order.getId()));
        }
        return orders;
    }

    @Override
    public Order update(Order order) {
        String query = "UPDATE orders SET fk_user_id = ? WHERE id = ? AND deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, order.getUserId());
            statement.setLong(2, order.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update order " + order, e);
        }
        return order;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE orders SET deleted = true WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete order with id="
                    + id + " is failed", e);
        }
    }

    private Order addProducts(Order order) {
        String query = "INSERT INTO orders_products(order_id, product_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            List<Product> products = order.getProducts();
            statement.setLong(1, order.getId());
            for (Product product : products) {
                statement.setLong(2, product.getId());
                statement.executeUpdate();
            }
            return order;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add products to order " + order, e);
        }
    }

    private List<Product> getProducts(Long orderId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products p JOIN orders_products op "
                + "ON p.id = op.product_id WHERE op.order_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long productId = resultSet.getLong("id");
                String productName = resultSet.getString("name");
                double productPrice = resultSet.getDouble("price");
                Product product = new Product(productName, productPrice);
                product.setId(productId);
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get products from order with id "
                    + orderId, e);
        }
    }

    private Order getOrder(ResultSet resultSet) throws SQLException {
        Long orderId = resultSet.getLong("id");
        Long userId = resultSet.getLong("fk_user_id");
        Order order = new Order(userId);
        order.setId(orderId);
        return order;
    }
}
