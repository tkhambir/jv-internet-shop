package com.internet.shop.dao.jdbc;

import com.internet.shop.dao.UserDao;
import com.internet.shop.lib.Dao;
import com.internet.shop.lib.exceptions.DataProcessingException;
import com.internet.shop.model.Role;
import com.internet.shop.model.User;
import com.internet.shop.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Dao
public class UserDaoJdbcImpl implements UserDao {
    @Override
    public Optional<User> findByLogin(String login) {
        String query = "SELECT * FROM users WHERE deleted = FALSE AND login = ?";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                return Optional.of(getUser(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Getting user by login "
                    + login + " is failed", e);
        }
    }

    @Override
    public User create(User user) {
        String query = "INSERT INTO users(name, login, password, salt) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setBytes(4, user.getSalt());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                user.setId(resultSet.getLong("GENERATED_KEY"));
            }
            return addRoles(user, connection);
        } catch (SQLException e) {
            throw new DataProcessingException("Creating user"
                    + user + " is failed", e);
        }
    }

    @Override
    public Optional<User> get(Long id) {
        String query = "SELECT * FROM users WHERE deleted = FALSE AND id = ?";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                return Optional.of(getUser(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Getting user by id="
                    + id + " is failed", e);
        }
    }

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users WHERE deleted = false;";
        List<User> users = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(getUser(resultSet));
            }
            return users;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get users from DB", e);
        }
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET name = ?, login = ?, password = ?, salt = ? "
                + "WHERE id = ? AND deleted = false;";

        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getName());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setBytes(4, user.getSalt());
            statement.setLong(5, user.getId());
            statement.executeUpdate();
            deleteRoles(user.getId());
            return addRoles(user, connection);
        } catch (SQLException e) {
            throw new DataProcessingException("Updating user with id="
                    + user.getId() + " is failed", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE users SET deleted = true WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Delete of user with id="
                    + id + " is failed", e);
        }
    }

    private void deleteRoles(Long userId) {
        String query = "DELETE FROM user_roles WHERE user_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
            return;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete roles of user with id "
                    + userId, e);
        }
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String password = resultSet.getString("password");
        byte[] salt = resultSet.getBytes("salt");
        User user = new User(name, login, password);
        user.setSalt(salt);
        user.setId(id);
        user.setRoles(getRoles(id));
        return user;
    }

    private Set<Role> getRoles(Long userId) {
        String query = "SELECT * FROM roles r JOIN user_roles ur ON r.id = ur.role_id "
                + "WHERE ur.user_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            Set<Role> roles = new HashSet<>();
            while (resultSet.next()) {
                Long roleId = resultSet.getLong("id");
                String roleName = resultSet.getString("role_name");
                Role role = Role.of(roleName);
                role.setId(roleId);
                roles.add(role);
            }
            return roles;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get roles of user with id "
                    + userId, e);
        }
    }

    private User addRoles(User user, Connection connection) throws SQLException {
        String query = "INSERT INTO user_roles(user_id, role_id) "
                + "VALUES(?,(SELECT id FROM roles WHERE role_name = ?))";
        PreparedStatement statement = connection.prepareStatement(query);
        for (Role role : user.getRoles()) {
            statement.setLong(1, user.getId());
            statement.setString(2, role.getRoleName().name());
            statement.executeUpdate();
        }
        return user;
    }
}
