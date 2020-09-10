package com.internet.shop.controllers;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.service.ProductService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProductsController extends HttpServlet {
    private static Injector injector = Injector.getInstance("com.internet.shop");
    private final ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI().split("/")[2];

        if (uri.equals("all")) {
            handleGetAllProducts(req, resp);
        } else if (uri.equals("add")) {
            req.getRequestDispatcher("/WEB-INF/views/products/add.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI().split("/")[2];

        if (uri.equals("add")) {
            String name = req.getParameter("name");
            String price = req.getParameter("price");
            Product product = new Product(name, Double.parseDouble(price));
            productService.create(product);
            handleGetAllProducts(req, resp);
        }
    }

    private void handleGetAllProducts(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Product> allProducts = productService.getAll();
        req.setAttribute("products", allProducts);
        req.getRequestDispatcher("/WEB-INF/views/products/all.jsp").forward(req,resp);
    }
}
