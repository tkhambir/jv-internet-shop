package com.internet.shop.dao.jdbc;

import com.internet.shop.dao.ShoppingCartDao;
import com.internet.shop.lib.Dao;
import com.internet.shop.lib.exceptions.DataProcessingException;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
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
public class ShoppingCardDaoJdbcImpl implements ShoppingCartDao {
    @Override
    public Optional<ShoppingCart> getByUserId(Long userId) {
        String query = "SELECT sc.id, sc.fk_user_id, p.id as priceId, p.name, p.price "
                + "FROM products p  "
                + "JOIN shopping_cart_products scp ON p.id = scp.product_id "
                + "RIGHT JOIN shopping_cart sc  ON sc.id = scp.cart_id WHERE sc.fk_user_id = ?";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();

            List<Product> products = new ArrayList<>();
            ShoppingCart shoppingCart = null;

            while (rs.next()) {
                if (rs.isLast()) {
                    shoppingCart = getCart(rs);
                    shoppingCart.setProducts(products);
                }

                Long priceId = rs.getLong("priceId");

                if (!rs.wasNull()) {
                    products.add(new Product(priceId, rs.getString("name"), rs.getDouble("price")));
                }
            }

            return Optional.ofNullable(shoppingCart);
        } catch (SQLException e) {
            throw new DataProcessingException("Getting cart by user id="
                    + userId + " is failed", e);
        }
    }

    @Override
    public ShoppingCart create(ShoppingCart cart) {
        String query = "INSERT INTO shopping_cart(fk_user_id) VALUES (?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, cart.getUserId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                cart.setId(resultSet.getLong("GENERATED_KEY"));
            }
            return cart;
        } catch (SQLException e) {
            throw new DataProcessingException("Creating cart"
                    + cart + " is failed", e);
        }
    }

    @Override
    public Optional<ShoppingCart> get(Long id) {
        String query = "SELECT sc.id, sc.fk_user_id, name, price FROM products p  "
                + "JOIN shopping_cart_products scp ON p.id = scp.product_id "
                + "RIGHT JOIN shopping_cart sc  ON sc.id = scp.cart_id WHERE scp.cart_id = ?";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            List<Product> products = new ArrayList<>();
            ShoppingCart shoppingCart = null;

            while (rs.next()) {
                if (rs.isLast()) {
                    shoppingCart = getCart(rs);
                    shoppingCart.setProducts(products);
                }
                products.add(new Product(rs.getLong("id"),
                        rs.getString("name"), rs.getDouble("price")));
            }

            return Optional.ofNullable(shoppingCart);
        } catch (SQLException e) {
            throw new DataProcessingException("Getting cart by id="
                    + id + " is failed", e);
        }
    }

    @Override
    public List<ShoppingCart> getAll() {
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        String query = "SELECT * FROM shopping_cart WHERE deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                shoppingCarts.add(getCart(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get carts from DB", e);
        }
        for (ShoppingCart shoppingCart : shoppingCarts) {
            shoppingCart.setProducts(getProducts(shoppingCart.getId()));
        }
        return shoppingCarts;
    }

    @Override
    public ShoppingCart update(ShoppingCart cart) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE shopping_cart SET deleted = true WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete cart with id="
                    + id + " is failed", e);
        }
    }

    public ShoppingCart addProduct(ShoppingCart cart, Product product) {
        String query = "INSERT INTO shopping_cart_products(cart_id, product_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, cart.getId());
            statement.setLong(2, product.getId());
            statement.executeUpdate();
            return cart;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add products to shopping cart " + cart, e);
        }
    }

    private List<Product> getProducts(Long cartId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products p LEFT JOIN shopping_cart_products scp "
                + "ON p.id = scp.product_id WHERE scp.cart_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, cartId);
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
            throw new DataProcessingException("Can't get products from cart with id "
                    + cartId, e);
        }
    }

    public void deleteProduct(ShoppingCart cart, Product product) {
        String query = "DELETE FROM shopping_cart_products WHERE cart_id = ? "
                + "AND product_id = ? LIMIT 1;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, cart.getId());
            statement.setLong(2, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete product from shopping cart "
                    + cart, e);
        }
    }

    @Override
    public void removeAllProducts(ShoppingCart cart) {
        String query = "DELETE FROM shopping_cart_products WHERE cart_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, cart.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete products from shopping cart "
                    + cart, e);
        }

    }

    private ShoppingCart getCart(ResultSet resultSet) throws SQLException {
        Long cartId = resultSet.getLong("id");
        Long userId = resultSet.getLong("fk_user_id");
        ShoppingCart cart = new ShoppingCart(userId);
        cart.setId(cartId);
        return cart;
    }
}
