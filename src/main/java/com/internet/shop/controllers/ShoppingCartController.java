package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.service.ProductService;
import com.internet.shop.service.ShoppingCartService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShoppingCartController extends HttpServlet {
    private static final Long USER_ID = 1L;

    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private final ShoppingCartService shoppingCartService =
            (ShoppingCartService) injector.getInstance(ShoppingCartService.class);
    private final ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI().split("/")[2];
        if (uri.equals("all")) {
            ShoppingCart shoppingCart = shoppingCartService.getByUserId(USER_ID);
            List<Product> allProducts = shoppingCart.getProducts();
            req.setAttribute("products", allProducts);
            req.getRequestDispatcher("/WEB-INF/views/shopping-cart/cart.jsp").forward(req, resp);
        } else if (uri.equals("add")) {
            Long productId = Long.valueOf(req.getParameter("productId"));
            ShoppingCart shoppingCart = shoppingCartService.getByUserId(USER_ID);
            Product product = productService.get(productId);
            shoppingCartService.addProduct(shoppingCart, product);
            List<Product> allProducts = productService.getAll();
            req.setAttribute("products", allProducts);
            req.getRequestDispatcher("/WEB-INF/views/products/all.jsp").forward(req,resp);

        }
    }
}
