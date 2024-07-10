package ru.clevertec.check.dao.impl;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DiscountCardDAOInDb implements DAO<DiscountCard, Integer> {

    private final ConnectionManager connectionManager;

    public DiscountCardDAOInDb(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Optional<DiscountCard> getBy(Integer discountCardNumber) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM public.discount_card WHERE number=?")) {
            statement.setInt(1, discountCardNumber);
            DiscountCard discountCard = null;
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                discountCard = new DiscountCard(
                        resultSet.getLong("id"),
                        discountCardNumber,
                        resultSet.getInt("amount"));
            }

            resultSet.close();
            return Optional.ofNullable(discountCard);
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }
}
