package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.model.User;
import com.internet.shop.service.ShoppingCartService;
import com.internet.shop.service.UserService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UsersController extends HttpServlet {
    private static Injector injector = Injector.getInstance("com.internet.shop");
    private final UserService userService = (UserService) injector.getInstance(UserService.class);
    private final ShoppingCartService shoppingCartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI().split("/")[2];

        if (uri.equals("all")) {
            handleGetAllUsers(req, resp);
        } else if (uri.equals("registration")) {
            req.getRequestDispatcher("/WEB-INF/views/users/registration.jsp").forward(req, resp);
        } else if (uri.equals("delete")) {
            Long userId = Long.valueOf(req.getParameter("userId"));
            userService.delete(userId);
            handleGetAllUsers(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI().split("/")[2];

        if (uri.equals("registration")) {
            String name = req.getParameter("name");
            String login = req.getParameter("login");
            String password = req.getParameter("pwd");
            String passwordRepeat = req.getParameter("pwd-repeat");
            if (password.equals(passwordRepeat)) {
                User user = new User(name, login, password);
                userService.create(user);
                shoppingCartService.create(new ShoppingCart(user.getId()));
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                req.setAttribute("message", "Your password and repeat password aren't the same");
                req.getRequestDispatcher("/WEB-INF/views/users/registration.jsp").forward(req,resp);
            }

        }
    }

    private void handleGetAllUsers(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<User> allUsers = userService.getAll();
        req.setAttribute("users", allUsers);
        req.getRequestDispatcher("/WEB-INF/views/users/all.jsp").forward(req,resp);
    }

}
