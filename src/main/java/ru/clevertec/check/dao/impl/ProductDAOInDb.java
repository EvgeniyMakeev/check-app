package ru.clevertec.check.dao.impl;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductDAOInDb implements DAO<Product, Long> {

    private final ConnectionManager connectionManager;

    public ProductDAOInDb(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Optional<Product> getBy(Long id) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM public.product WHERE id=?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Product product = new Product(id,
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity_in_stock"),
                        resultSet.getBoolean("wholesale_product"));
                resultSet.close();
                return Optional.of(product);
            } else {
                resultSet.close();
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }
}
