package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.service.ProductService;
import com.internet.shop.service.ShoppingCartService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeleteProductFromCartController extends HttpServlet {
    private static final String USER_ID = "userId";
    private static Injector injector = Injector.getInstance("com.internet.shop");
    private final ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);
    private final ShoppingCartService shoppingCartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute(USER_ID);
        Long productId = Long.valueOf(req.getParameter("productId"));
        shoppingCartService.deleteProduct(shoppingCartService.getByUserId(userId),
                productService.get(productId));
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
