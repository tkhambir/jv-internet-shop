package com.internet.shop.dao.jdbc;

import com.internet.shop.dao.ProductDao;
import com.internet.shop.lib.Dao;
import com.internet.shop.lib.exceptions.DataProcessingException;
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
public class ProductDaoJdbcImpl implements ProductDao {

    @Override
    public Product create(Product product) {
        String query = "INSERT INTO PRODUCTS(NAME, PRICE) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice().doubleValue());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                product.setId(resultSet.getLong("GENERATED_KEY"));
            }
            return product;
        } catch (SQLException e) {
            throw new DataProcessingException("Creating product"
                    + product + " is failed", e);
        }
    }

    @Override
    public Optional<Product> get(Long id) {
        String query = "SELECT * FROM PRODUCTS WHERE ID = ? AND DELETED = false;";

        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("NAME");
                double price = resultSet.getDouble("PRICE");
                Product product = new Product(name, price);
                product.setId(id);
                return Optional.of(product);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Getting product by id="
                    + id + " is failed", e);
        }
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM PRODUCTS WHERE DELETED = false;";
        List<Product> products = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                double price = resultSet.getDouble("PRICE");
                Product product = new Product(name, price);
                product.setId(id);
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get product from DB", e);
        }
    }

    @Override
    public Product update(Product product) {
        String query = "UPDATE PRODUCTS SET NAME = ?, PRICE = ? WHERE ID = ? AND DELETED = false;";

        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice().doubleValue());
            statement.setLong(3, product.getId());
            statement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new DataProcessingException("Updating product with id="
                    + product.getId() + " is failed", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE PRODUCTS SET DELETED = true WHERE ID = ?;";

        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DataProcessingException("Delete of product with id="
                    + id + " is failed", e);
        }
    }
}
