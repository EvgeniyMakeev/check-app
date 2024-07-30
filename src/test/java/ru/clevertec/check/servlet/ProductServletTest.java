package ru.clevertec.check.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.service.ProductService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServletTest {

    @Mock
    private ProductService productService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductServlet productServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ProductServlet test: GET - Should return product if ID is valid")
    void testDoGet_ValidRequest() throws Exception {
        Product product = new Product(1L, "Test Product", 100.0, 50, true);
        when(request.getParameter("id")).thenReturn("1");
        when(productService.getProductById(1L)).thenReturn(product);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        productServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValue(any(Writer.class), eq(product));
    }

    @Test
    @DisplayName("ProductServlet test: GET - Should return 404 if product not found")
    void testDoGet_ProductNotFound() throws Exception {
        when(request.getParameter("id")).thenReturn("2");
        when(productService.getProductById(2L)).thenThrow(new AnyProblemsWithProductOrEnteringArgumentsException());

        productServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("ProductServlet test: POST - Should add product if request is valid")
    void testDoPost_ValidRequest() throws Exception {
        String jsonRequest = """
                {
                    "id": 1,
                    "description": "New Product",
                    "price": 100.0,
                    "quantityInStock": 50,
                    "isWholesale": true
                }
                """;
        InputStream inputStream = new ByteArrayInputStream(jsonRequest.getBytes());
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.read(any(byte[].class))).thenAnswer(invocation -> inputStream.read((byte[]) invocation.getArguments()[0]));

        Product product = new Product(1L, "New Product", 100.0, 50, true);
        when(objectMapper.readValue(any(InputStream.class), eq(Product.class))).thenReturn(product);
        doNothing().when(productService).addProduct(product);

        productServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("ProductServlet test: PUT - Should update product if request is valid")
    void testDoPut_ValidRequest() throws Exception {
        String jsonRequest = """
                {
                    "id": 1,
                    "description": "Updated Product",
                    "price": 200.0,
                    "quantityInStock": 100,
                    "isWholesale": false
                }
                """;
        InputStream inputStream = new ByteArrayInputStream(jsonRequest.getBytes());
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.read(any(byte[].class))).thenAnswer(invocation -> inputStream.read((byte[]) invocation.getArguments()[0]));

        when(request.getParameter("id")).thenReturn("1");
        Product product = new Product(1L, "Updated Product", 200.0, 100, false);
        when(objectMapper.readValue(any(InputStream.class), eq(Product.class))).thenReturn(product);
        doNothing().when(productService).updateProduct(1L, product);

        productServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("ProductServlet test: DELETE - Should delete product if request is valid")
    void testDoDelete_ValidRequest() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        doNothing().when(productService).deleteProduct(1L);

        productServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
