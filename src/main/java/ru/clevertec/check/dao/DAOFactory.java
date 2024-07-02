package ru.clevertec.check.dao;

import ru.clevertec.check.dao.impl.DiscountCardDAOInMemory;
import ru.clevertec.check.dao.impl.ProductDAOInMemory;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.model.Product;

public class DAOFactory {
    public static DAO<Product> createProductDAO() {
        return new ProductDAOInMemory();
    }

    public static DAO<DiscountCard> createDiscountCardDAO() {
        return new DiscountCardDAOInMemory();
    }
}