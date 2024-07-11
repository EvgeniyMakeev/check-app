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
import java.util.List;
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
            statement.execute("CREATE TABLE public.product (id BIGINT AUTO_INCREMENT PRIMARY KEY, description VARCHAR(255), price DOUBLE, quantity_in_stock INT, wholesale_product BOOLEAN)");
            statement.execute("INSERT INTO public.product (description, price, quantity_in_stock, wholesale_product) VALUES ('Product 1', 100.0, 50, true)");
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

    @Test
    void add_shouldAddProduct() throws AnyOtherException {
        Product newProduct = new Product("Product 2", 200.0, 30, false);
        productDAOInDb.add(newProduct);

        Optional<Product> result = productDAOInDb.getBy(2L);

        assertThat(result).isPresent();
        assertThat(result.get().description()).isEqualTo("Product 2");
        assertThat(result.get().price()).isEqualTo(200.0);
        assertThat(result.get().quantityInStock()).isEqualTo(30);
        assertThat(result.get().isWholesale()).isFalse();
    }

    @Test
    void getAll_shouldReturnAllProducts() throws AnyOtherException {
        Product newProduct = new Product("Product 2", 200.0, 30, false);
        productDAOInDb.add(newProduct);

        List<Product> products = productDAOInDb.getAll();

        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::description).containsExactlyInAnyOrder("Product 1", "Product 2");
    }

    @Test
    void update_shouldUpdateProduct() throws AnyOtherException {
        Product updatedProduct = new Product(1L, "Updated Product 1", 150.0, 40, true);
        productDAOInDb.update(updatedProduct);

        Optional<Product> result = productDAOInDb.getBy(1L);

        assertThat(result).isPresent();
        assertThat(result.get().description()).isEqualTo("Updated Product 1");
        assertThat(result.get().price()).isEqualTo(150.0);
        assertThat(result.get().quantityInStock()).isEqualTo(40);
        assertThat(result.get().isWholesale()).isTrue();
    }

    @Test
    void delete_shouldRemoveProduct() throws AnyOtherException {
        productDAOInDb.delete(1L);

        Optional<Product> result = productDAOInDb.getBy(1L);

        assertThat(result).isNotPresent();
    }
}
