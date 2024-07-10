package ru.clevertec.check.dao.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.util.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCardDAOInDbTest {

    private ConnectionManager connectionManager;
    private DiscountCardDAOInDb discountCardDAOInDb;

    @BeforeEach
    void setUp() throws SQLException {
        connectionManager = () -> DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

        discountCardDAOInDb = new DiscountCardDAOInDb(connectionManager);

        try (Connection connection = connectionManager.open(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE public.discount_card (id INT PRIMARY KEY, number INT, amount INT)");
            statement.execute("INSERT INTO public.discount_card (id, number, amount) VALUES (1, 12345, 10)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection connection = connectionManager.open(); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE public.discount_card");
        }
    }

    @Test
    void getBy_shouldReturnDiscountCard() throws AnyOtherException {
        Optional<DiscountCard> result = discountCardDAOInDb.getBy(12345);

        assertThat(result).isPresent();
        assertThat(result.get().number()).isEqualTo(12345);
        assertThat(result.get().discountAmount()).isEqualTo(10);
    }

    @Test
    void getBy_shouldReturnEmptyForNonExistingDiscountCard() throws AnyOtherException {
        Optional<DiscountCard> result = discountCardDAOInDb.getBy(54321);

        assertThat(result).isNotPresent();
    }
}
