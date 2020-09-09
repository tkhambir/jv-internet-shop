package com.internet.shop;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Product;
import com.internet.shop.model.ShoppingCart;
import com.internet.shop.model.User;
import com.internet.shop.service.OrderService;
import com.internet.shop.service.ProductService;
import com.internet.shop.service.ShoppingCartService;
import com.internet.shop.service.UserService;
import java.math.BigDecimal;

public class WebShopApp {
    private static Injector injector = Injector.getInstance("com.internet.shop");

    public static void main(String[] args) {
        ProductService productService = (ProductService) injector.getInstance(ProductService.class);

        productService.create(new Product("iphone X", 1000));
        productService.create(new Product("Iphone 11", 1200));
        productService.create(new Product("Iphone 9", 800));
        System.out.println("All products: ");

        productService.getAll().forEach(System.out::println);

        Product productToUpdate = productService.get(3L);
        System.out.println("Changing price for Iphone 9: ");
        System.out.println(productToUpdate);
        productToUpdate.setPrice(BigDecimal.valueOf(650.0));
        System.out.println(productService.update(productToUpdate));

        productService.delete(3L);
        System.out.println("All products after removing Iphone 9: ");

        productService.getAll().forEach(System.out::println);

        System.out.println();

        UserService userService = (UserService) injector.getInstance(UserService.class);
        ShoppingCartService shoppingCartService
                = (ShoppingCartService) injector.getInstance(ShoppingCartService.class);
        userService.create(new User("User1", "user1", "111111"));
        shoppingCartService.create(new ShoppingCart(1L));
        userService.create(new User("User2", "user2", "222222"));
        shoppingCartService.create(new ShoppingCart(2L));
        userService.create(new User("User3", "user3", "333333"));
        shoppingCartService.create(new ShoppingCart(3L));

        System.out.println("All users: ");
        userService.getAll().forEach(System.out::println);

        User userToUpdate = userService.get(3L);
        System.out.println("Changing login for User3: ");
        System.out.println(userToUpdate);
        userToUpdate.setLogin("updated login");
        System.out.println(userService.update(userToUpdate));

        System.out.println("All users after removing User3: ");
        userService.delete(3L);
        shoppingCartService.delete(3L);
        userService.getAll().forEach(System.out::println);

        System.out.println();

        shoppingCartService.create(new ShoppingCart(1L));
        shoppingCartService.create(new ShoppingCart(2L));

        shoppingCartService.addProduct(shoppingCartService
                .getByUserId(1L), productService.get(1L));
        shoppingCartService.addProduct(shoppingCartService
                .getByUserId(1L), productService.get(2L));
        shoppingCartService.addProduct(shoppingCartService
                .getByUserId(2L), productService.get(1L));

        System.out.println("Shopping cart for User1: ");
        System.out.println(shoppingCartService.getByUserId(1L));
        System.out.println("Shopping cart for User2: ");
        System.out.println(shoppingCartService.getByUserId(2L));
        System.out.println("Shopping cart for User1 after removing iphone X: ");
        System.out.println(shoppingCartService.getByUserId(1L));
        shoppingCartService.deleteProduct(shoppingCartService
                .getByUserId(1L),productService.get(1L));
        System.out.println(shoppingCartService.getByUserId(1L));
        System.out.println();

        OrderService orderService = (OrderService) injector.getInstance(OrderService.class);
        orderService.completeOrder(shoppingCartService.getByUserId(1L));
        shoppingCartService.addProduct(shoppingCartService.getByUserId(1L),
                productService.get(2L));
        orderService.completeOrder(shoppingCartService.getByUserId(1L));
        orderService.completeOrder(shoppingCartService.getByUserId(2L));

        System.out.println("All orders: ");
        orderService.getAll().forEach(System.out::println);

        System.out.println("All orders for User1: ");
        orderService.getAll().forEach(System.out::println);

        System.out.println("All orders after removing: ");
        orderService.delete(2L);
        orderService.getAll().forEach(System.out::println);
    }
}
