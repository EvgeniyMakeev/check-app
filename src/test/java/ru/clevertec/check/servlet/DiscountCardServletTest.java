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
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.service.DiscountCardService;

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

class DiscountCardServletTest {

    @Mock
    private DiscountCardService discountCardService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DiscountCardServlet discountCardServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("DiscountCardServlet test: GET - Should return discount card if ID is valid")
    void testDoGet_ValidRequest() throws Exception {
        DiscountCard discountCard = new DiscountCard(1L, 1234, 5);
        when(request.getParameter("id")).thenReturn("1");
        when(discountCardService.getDiscountCardById(1L)).thenReturn(discountCard);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        discountCardServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValue(any(Writer.class), eq(discountCard));
    }

    @Test
    @DisplayName("DiscountCardServlet test: GET - Should return 404 if discount card not found")
    void testDoGet_DiscountCardNotFound() throws Exception {
        when(request.getParameter("id")).thenReturn("2");
        when(discountCardService.getDiscountCardById(2L)).thenThrow(new AnyProblemsWithProductOrEnteringArgumentsException());

        discountCardServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("DiscountCardServlet test: POST - Should add discount card if request is valid")
    void testDoPost_ValidRequest() throws Exception {
        String jsonRequest = """
                {
                    "id": 1,
                    "cardNumber": "Card123",
                    "discountRate": 0.15
                }
                """;
        InputStream inputStream = new ByteArrayInputStream(jsonRequest.getBytes());
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.read(any(byte[].class))).thenAnswer(invocation -> inputStream.read((byte[]) invocation.getArguments()[0]));

        DiscountCard discountCard = new DiscountCard(1L, 1234, 5);
        when(objectMapper.readValue(any(InputStream.class), eq(DiscountCard.class))).thenReturn(discountCard);
        doNothing().when(discountCardService).addDiscountCard(discountCard);

        discountCardServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("DiscountCardServlet test: PUT - Should update discount card if request is valid")
    void testDoPut_ValidRequest() throws Exception {
        String jsonRequest = """
                {
                    "id": 1,
                    "cardNumber": "UpdatedCard123",
                    "discountRate": 0.2
                }
                """;
        InputStream inputStream = new ByteArrayInputStream(jsonRequest.getBytes());
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.read(any(byte[].class))).thenAnswer(invocation -> inputStream.read((byte[]) invocation.getArguments()[0]));

        when(request.getParameter("id")).thenReturn("1");
        DiscountCard discountCard = new DiscountCard(1L, 7777, 2);
        when(objectMapper.readValue(any(InputStream.class), eq(DiscountCard.class))).thenReturn(discountCard);
        doNothing().when(discountCardService).updateDiscountCard(1L, discountCard);

        discountCardServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("DiscountCardServlet test: DELETE - Should delete discount card if request is valid")
    void testDoDelete_ValidRequest() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        doNothing().when(discountCardService).deleteDiscountCard(1L);

        discountCardServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
