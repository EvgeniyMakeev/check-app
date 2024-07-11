package ru.clevertec.check.dao.impl;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOInDb implements DAO<Product> {

    private final ConnectionManager connectionManager;

    public ProductDAOInDb(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void add(Product product) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("""
                             INSERT INTO public.product
                             (description, price, quantity_in_stock, wholesale_product)
                             VALUES (?,?,?,?)""")) {
            statement.setString(1, product.description());
            statement.setDouble(2, product.price());
            statement.setInt(3, product.quantityInStock());
            statement.setBoolean(4, product.isWholesale());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }

    @Override
    public Optional<Product> getBy(long id) throws AnyOtherException {
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

    @Override
    public List<Product> getAll() throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM public.product?")) {
            ResultSet resultSet = statement.executeQuery();

            List<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getLong("id"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity_in_stock"),
                        resultSet.getBoolean("wholesale_product"));
                products.add(product);
            }
            resultSet.close();
            return products;
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }

    @Override
    public void update(Product product) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("""
                         UPDATE public.product
                         SET description=?, price=?, quantity_in_stock=?, wholesale_product=?
                         WHERE id=?""")) {
            statement.setString(1, product.description());
            statement.setDouble(2, product.price());
            statement.setInt(3, product.quantityInStock());
            statement.setBoolean(4, product.isWholesale());
            statement.setLong(5, product.id());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }


    @Override
    public void delete(long id) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("DELETE FROM public.product WHERE id=?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }
}
