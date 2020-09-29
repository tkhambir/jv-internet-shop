package com.internet.shop.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private BigDecimal price;

    public Product(Long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = BigDecimal.valueOf(price);
    }

    public Product(String name, double price) {
        this.name = name;
        this.price = BigDecimal.valueOf(price);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", price=" + price
                + '}';
    }
}
