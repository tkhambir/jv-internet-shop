package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.service.ProductService;
import com.internet.shop.service.ShoppingCartService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddProductToCartController extends HttpServlet {
    private static final Long USER_ID = 1L;

    private static Injector injector = Injector.getInstance("com.internet.shop");
    private final ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);
    private final ShoppingCartService shoppingCartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long productId = Long.valueOf(req.getParameter("productId"));
        ShoppingCart shoppingCart = shoppingCartService.getByUserId(USER_ID);
        Product product = productService.get(productId);
        shoppingCartService.addProduct(shoppingCart, product);
        resp.sendRedirect(req.getContextPath() + "/products");
    }
}
