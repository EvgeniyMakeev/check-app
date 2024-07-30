package ru.clevertec.check.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.check.dao.DAOFactory;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.service.ProductService;
import ru.clevertec.check.util.impl.ConnectionManagerImpl;

import java.io.IOException;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private ProductService productService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        productService = new ProductService(DAOFactory.createProductDAO(new ConnectionManagerImpl()));
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            long id = Long.parseLong(request.getParameter("id"));
            Product product = productService.getProductById(id);
            objectMapper.writeValue(response.getWriter(), product);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (AnyProblemsWithProductOrEnteringArgumentsException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (AnyOtherException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Product product = objectMapper.readValue(request.getInputStream(), Product.class);
            if (product != null) {
                productService.addProduct(product);
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (AnyOtherException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            Product product = objectMapper.readValue(request.getInputStream(), Product.class);
            if (product != null) {
                productService.updateProduct(id, product);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (AnyOtherException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            productService.deleteProduct(id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (AnyOtherException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
