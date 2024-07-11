package ru.clevertec.check.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.check.dao.DAOFactory;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.service.DiscountCardService;
import ru.clevertec.check.util.impl.ConnectionManagerImpl;

import java.io.IOException;

@WebServlet("/discountcards")
public class DiscountCardServlet extends HttpServlet {

    private DiscountCardService discountCardService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        discountCardService = new DiscountCardService(DAOFactory.createDiscountCardDAO(new ConnectionManagerImpl()));
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            long id = Long.parseLong(request.getParameter("id"));
            DiscountCard discountCard = discountCardService.getDiscountCardById(id);
            objectMapper.writeValue(response.getWriter(), discountCard);
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
            DiscountCard discountCard = objectMapper.readValue(request.getInputStream(), DiscountCard.class);
            if (discountCard != null) {
                discountCardService.addDiscountCard(discountCard);
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
            DiscountCard discountCard = objectMapper.readValue(request.getInputStream(), DiscountCard.class);
            if (discountCard != null) {
                discountCardService.updateDiscountCard(id, discountCard);
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
            discountCardService.deleteDiscountCard(id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (AnyOtherException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
