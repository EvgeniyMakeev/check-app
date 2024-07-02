package ru.clevertec.check.dao.impl;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductDAOInMemory implements DAO<Product> {
    private final Map<Long, Product> products = new HashMap<>();

    @Override
    public void add(Product newProduct) {
        products.put(newProduct.id(), newProduct);
    }

    @Override
    public Optional<Product> getById(long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> getAll() {
        return products.values().stream().toList();
    }
}
