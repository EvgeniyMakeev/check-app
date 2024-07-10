package ru.clevertec.check.dao;

import ru.clevertec.check.dao.impl.DiscountCardDAOInDb;
import ru.clevertec.check.dao.impl.ProductDAOInDb;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.util.ConnectionManager;

public class DAOFactory {
    public static DAO<Product, Long> createProductDAO(ConnectionManager connectionManager) {
        return new ProductDAOInDb(connectionManager);
    }

    public static DAO<DiscountCard, Integer> createDiscountCardDAO(ConnectionManager connectionManager) {
        return new DiscountCardDAOInDb(connectionManager);
    }
}
