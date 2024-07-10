package ru.clevertec.check.service;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.Product;

public class ProductService {
    private final DAO<Product, Long> productDao;

    public ProductService(DAO<Product, Long> productDao) {
        this.productDao = productDao;
    }


    public Product getProductById(long productId)
            throws AnyProblemsWithProductOrEnteringArgumentsException, AnyOtherException {
        return productDao.getBy(productId).orElseThrow(AnyProblemsWithProductOrEnteringArgumentsException::new);
    }
}
