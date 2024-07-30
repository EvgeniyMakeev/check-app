package ru.clevertec.check.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.Product;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private Product mockProduct;

    @Mock
    private DAO<Product> productDao;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductById_shouldReturnProduct() throws AnyOtherException, AnyProblemsWithProductOrEnteringArgumentsException {
        when(productDao.getBy(1L)).thenReturn(Optional.of(mockProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isEqualTo(mockProduct);
        verify(productDao, times(1)).getBy(1L);
        assertDoesNotThrow(() -> productService.getProductById(1L));
    }

    @Test
    void getProductById_shouldThrowException() throws AnyOtherException {
        when(productDao.getBy(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(2L))
                .isInstanceOf(AnyProblemsWithProductOrEnteringArgumentsException.class);

        verify(productDao, times(1)).getBy(2L);
    }

    @Test
    void addProduct_shouldAddProduct() throws AnyOtherException {
        Product newProduct = new Product(1L, "New Product", 100.0, 50, true);
        doNothing().when(productDao).add(newProduct);

        productService.addProduct(newProduct);

        verify(productDao, times(1)).add(newProduct);
    }

    @Test
    void updateProduct_shouldUpdateProduct() throws AnyOtherException {
        Product updatedProduct = new Product(1L, "Updated Product", 200.0, 100, false);
        doNothing().when(productDao).update(updatedProduct);

        productService.updateProduct(1L, updatedProduct);

        verify(productDao, times(1)).update(updatedProduct);
    }

    @Test
    void deleteProduct_shouldDeleteProduct() throws AnyOtherException {
        doNothing().when(productDao).delete(1L);

        productService.deleteProduct(1L);

        verify(productDao, times(1)).delete(1L);
    }
}
