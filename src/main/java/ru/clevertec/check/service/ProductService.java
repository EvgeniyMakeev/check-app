package ru.clevertec.check.service;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.Product;

public class ProductService {
    private final DAO<Product> productDao;

    public ProductService(DAO<Product> productDao) {
        this.productDao = productDao;
    }

    public void addProduct(Product product) throws AnyOtherException {
        productDao.add(product);
    }

    public Product getProductById(long productId)
            throws AnyProblemsWithProductOrEnteringArgumentsException, AnyOtherException {
        return productDao.getBy(productId).orElseThrow(AnyProblemsWithProductOrEnteringArgumentsException::new);
    }

    public void updateProduct(long id, Product product) throws AnyOtherException {
        productDao.update(new Product(id, product.description(), product.price(), product.quantityInStock(), product.isWholesale()));
    }

    public void deleteProduct(long id) throws AnyOtherException {
        productDao.delete(id);
    }
}
