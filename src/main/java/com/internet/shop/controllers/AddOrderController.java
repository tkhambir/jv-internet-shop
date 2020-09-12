package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.service.OrderService;
import com.internet.shop.service.ShoppingCartService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddOrderController extends HttpServlet {
    private static final Long USER_ID = 1L;

    private static Injector injector = Injector.getInstance("com.internet.shop");
    private final OrderService orderService = (OrderService) injector
            .getInstance(OrderService.class);
    private final ShoppingCartService shoppingCartService =
            (ShoppingCartService) injector.getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        orderService.completeOrder(shoppingCartService.getByUserId(USER_ID));
        resp.sendRedirect(req.getContextPath() + "/orders");
    }
}
