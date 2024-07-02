package ru.clevertec.check.service;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProductService {
    private final DAO<Product> productDao;

    public ProductService(DAO<Product> productDao) {
        this.productDao = productDao;
    }

    public void readProductsFromCSV(String productsFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(productsFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                long id = Long.parseLong(values[0]);
                String description = values[1];
                double price = Double.parseDouble(values[2]);
                int quantityInStock = Integer.parseInt(values[3]);
                boolean wholesaleProduct = values[4].equals("+");
                productDao.add(new Product(id, description, price, quantityInStock, wholesaleProduct));
            }
        }
    }

    public Product getProductById(long productId) throws AnyProblemsWithProductOrEnteringArgumentsException {
        return productDao.getById(productId).orElseThrow(AnyProblemsWithProductOrEnteringArgumentsException::new);
    }
}
