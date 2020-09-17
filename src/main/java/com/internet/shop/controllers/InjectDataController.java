package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.model.Role;
import com.internet.shop.model.User;
import com.internet.shop.service.ProductService;
import com.internet.shop.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class InjectDataController extends HttpServlet {
    private static Injector injector = Injector.getInstance("com.internet.shop");
    private ProductService productService =
            (ProductService) injector.getInstance(ProductService.class);
    private UserService userService = (UserService) injector.getInstance(UserService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        productService.create(new Product("Product 1", 123.25));
        productService.create(new Product("Product 2", 1183.25));
        productService.create(new Product("Product 3", 355.99));

        User user1 = new User("Admin 1", "admin1", "1111");
        user1.setRoles(Set.of(Role.of("ADMIN")));
        User user2 = new User("Admin 2", "admin2", "2222");
        user2.setRoles(Set.of(Role.of("ADMIN")));
        userService.create(user1);
        userService.create(user2);
        resp.sendRedirect(req.getContextPath() + "/");
    }
}
