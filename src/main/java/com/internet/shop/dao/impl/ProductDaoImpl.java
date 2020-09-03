package com.internet.shop.dao.impl;

import com.internet.shop.dao.ProductDao;
import com.internet.shop.db.Storage;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.Product;
import java.util.List;
import java.util.Optional;

@Dao
public class ProductDaoImpl implements ProductDao {
    @Override
    public Product create(Product product) {
        Storage.addProduct(product);
        return product;
    }

    @Override
    public Optional<Product> getById(Long productId) {
        return Storage.products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst();
    }

    @Override
    public Product update(Product product) {
        for (int i = 0; i < Storage.products.size(); i++) {
            if (Storage.products.get(i).getId().equals(product.getId())) {
                Storage.products.set(i, product);
            }
        }
        return product;
    }

    @Override
    public boolean delete(Product product) {
        return Storage.products.removeIf(p -> p.getId().equals(product.getId()));
    }

    @Override
    public List<Product> getAllProducts() {
        return Storage.products;
    }
}
