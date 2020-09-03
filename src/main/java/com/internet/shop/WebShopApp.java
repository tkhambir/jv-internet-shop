package com.internet.shop;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.service.ProductService;
import java.math.BigDecimal;

public class WebShopApp {
    private static Injector injector = Injector.getInstance("com.internet.shop");

    public static void main(String[] args) {
        ProductService productService = (ProductService) injector.getInstance(ProductService.class);

        productService.create(new Product("iphone X", 1000));
        productService.create(new Product("Iphone 11", 1200));
        productService.create(new Product("Iphone 9", 800));
        System.out.println("All products: ");
        for (Product product : productService.getAllProducts()) {
            System.out.println(product);
        }

        Product productToUpdate = productService.getById(3L);
        System.out.println("Changing price for Iphone 9: ");
        System.out.println(productToUpdate);
        productToUpdate.setPrice(BigDecimal.valueOf(650.0));
        System.out.println();
        System.out.println(productService.update(productToUpdate));

        productService.delete(productToUpdate);
        System.out.println("All products after removing Iphone 9: ");
        for (Product product : productService.getAllProducts()) {
            System.out.println(product);
        }
    }
}
