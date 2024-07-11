package ru.clevertec.check.dao.impl;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscountCardDAOInDb implements DAO<DiscountCard> {

    private final ConnectionManager connectionManager;

    public DiscountCardDAOInDb(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void add(DiscountCard discountCard) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("INSERT INTO public.discount_card (number, discount_amount)VALUES (?,?)")) {
            statement.setInt(1, discountCard.number());
            statement.setInt(2, discountCard.discountAmount());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }

    @Override
    public Optional<DiscountCard> getBy(long id) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM public.discount_card WHERE id=?")) {
            statement.setLong(1, id);
            return getDiscountCard(statement);
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }

    private Optional<DiscountCard> getDiscountCard(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            DiscountCard discountCard = new DiscountCard(
                    resultSet.getLong("id"),
                    resultSet.getInt("number"),
                    resultSet.getInt("discount_amount"));
            resultSet.close();
            return Optional.of(discountCard);
        } else {
            resultSet.close();
            return Optional.empty();
        }
    }

    @Override
    public List<DiscountCard> getAll() throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM public.discount_card")) {
            ResultSet resultSet = statement.executeQuery();

            List<DiscountCard> discountCards = new ArrayList<>();
            while (resultSet.next()) {
                DiscountCard discountCard = new DiscountCard(
                        resultSet.getLong("id"),
                        resultSet.getInt("number"),
                        resultSet.getInt("discount_amount"));
                discountCards.add(discountCard);
            }
            resultSet.close();
            return discountCards;
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }

    @Override
    public void update(DiscountCard discountCard) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("UPDATE public.discount_card SET number=?, discount_amount=? WHERE id=?")) {
            statement.setInt(1, discountCard.number());
            statement.setInt(2, discountCard.discountAmount());
            statement.setLong(3, discountCard.id());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }

    @Override
    public void delete(long id) throws AnyOtherException {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement =
                     connection.prepareStatement("DELETE FROM public.discount_card WHERE id=?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AnyOtherException();
        }
    }
}
