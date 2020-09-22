package com.internet.shop;

import com.internet.shop.lib.Injector;
import com.internet.shop.service.ProductService;

public class WebShopApp {
    private static Injector injector = Injector.getInstance("com.internet.shop");

    public static void main(String[] args) {
        ProductService productService = (ProductService) injector.getInstance(ProductService.class);


    }
}
