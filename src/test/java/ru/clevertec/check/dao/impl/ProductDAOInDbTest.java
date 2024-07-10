package ru.clevertec.check.dao.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.util.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDAOInDbTest {

    private ConnectionManager connectionManager;
    private ProductDAOInDb productDAOInDb;

    @BeforeEach
    void setUp() throws SQLException {
        connectionManager = () -> DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

        productDAOInDb = new ProductDAOInDb(connectionManager);

        try (Connection connection = connectionManager.open(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE public.product (id BIGINT PRIMARY KEY, description VARCHAR(255), price DOUBLE, quantity_in_stock INT, wholesale_product BOOLEAN)");
            statement.execute("INSERT INTO public.product (id, description, price, quantity_in_stock, wholesale_product) VALUES (1, 'Product 1', 100.0, 50, true)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection connection = connectionManager.open(); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE public.product");
        }
    }

    @Test
    void getBy_shouldReturnProduct() throws AnyOtherException {
        Optional<Product> result = productDAOInDb.getBy(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().description()).isEqualTo("Product 1");
        assertThat(result.get().price()).isEqualTo(100.0);
        assertThat(result.get().quantityInStock()).isEqualTo(50);
        assertThat(result.get().isWholesale()).isTrue();
    }

    @Test
    void getBy_shouldReturnEmptyForNonExistingProduct() throws AnyOtherException {
        Optional<Product> result = productDAOInDb.getBy(2L);

        assertThat(result).isNotPresent();
    }
}
