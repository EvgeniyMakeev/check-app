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
import java.util.List;
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
            statement.execute("CREATE TABLE public.discount_card (id INT PRIMARY KEY AUTO_INCREMENT, number INT, discount_amount INT)");
            statement.execute("INSERT INTO public.discount_card (number, discount_amount) VALUES (1234, 10)");
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
        Optional<DiscountCard> result = discountCardDAOInDb.getBy(1);

        assertThat(result).isPresent();
        assertThat(result.get().number()).isEqualTo(1234);
        assertThat(result.get().discountAmount()).isEqualTo(10);
    }

    @Test
    void getBy_shouldReturnEmptyForNonExistingDiscountCard() throws AnyOtherException {
        Optional<DiscountCard> result = discountCardDAOInDb.getBy(5432);

        assertThat(result).isNotPresent();
    }

    @Test
    void add_shouldAddDiscountCard() throws AnyOtherException {
        DiscountCard newCard = new DiscountCard(2, 5678, 20);
        discountCardDAOInDb.add(newCard);

        Optional<DiscountCard> result = discountCardDAOInDb.getBy(2);

        assertThat(result).isPresent();
        assertThat(result.get().number()).isEqualTo(5678);
        assertThat(result.get().discountAmount()).isEqualTo(20);
    }

    @Test
    void getAll_shouldReturnAllDiscountCards() throws AnyOtherException {
        DiscountCard newCard = new DiscountCard(2, 5678, 20);
        discountCardDAOInDb.add(newCard);

        List<DiscountCard> discountCards = discountCardDAOInDb.getAll();

        assertThat(discountCards).hasSize(2);
    }

    @Test
    void update_shouldUpdateDiscountCard() throws AnyOtherException {
        DiscountCard card = new DiscountCard(1, 1234, 15);
        discountCardDAOInDb.update(card);

        Optional<DiscountCard> updatedCardOptional = discountCardDAOInDb.getBy(1);
        assertThat(updatedCardOptional).isPresent();
        assertThat(updatedCardOptional.get().discountAmount()).isEqualTo(15);
    }

    @Test
    void delete_shouldRemoveDiscountCard() throws AnyOtherException {
        Optional<DiscountCard> cardOptional = discountCardDAOInDb.getBy(1);
        assertThat(cardOptional).isPresent();

        discountCardDAOInDb.delete(cardOptional.get().id());

        Optional<DiscountCard> deletedCardOptional = discountCardDAOInDb.getBy(1234);
        assertThat(deletedCardOptional).isNotPresent();
    }
}
