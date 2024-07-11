package ru.clevertec.check.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.check.dao.DAOFactory;
import ru.clevertec.check.dto.ProductForBayDTO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.exception.NotEnoughMoneyException;
import ru.clevertec.check.dto.PurchaseDTO;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.service.CheckService;
import ru.clevertec.check.service.DiscountCardService;
import ru.clevertec.check.service.ProductService;
import ru.clevertec.check.util.ConnectionManager;
import ru.clevertec.check.util.impl.ConnectionManagerImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@WebServlet("/check")
public class CheckServlet extends HttpServlet {

    private final String csvFilePath = "result.csv";

    private CheckService checkService;
    private DiscountCardService discountCardService;
    private ProductService productService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ConnectionManager connectionManager = new ConnectionManagerImpl();
        discountCardService = new DiscountCardService(DAOFactory.createDiscountCardDAO(connectionManager));
        productService = new ProductService(DAOFactory.createProductDAO(connectionManager));
        checkService = new CheckService(csvFilePath);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            PurchaseDTO purchaseDTO = objectMapper.readValue(request.getInputStream(), PurchaseDTO.class);

            if (purchaseDTO != null) {
                DiscountCard discountCard = discountCardService.getDiscountCardByNumber(purchaseDTO.discountCardNumber());
                Map<Product, Integer> shoppingCart = getShoppingCart(purchaseDTO.products());

                checkService.makeCheck(discountCard,shoppingCart,purchaseDTO.balanceDebitCard());

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + csvFilePath + "\"");

                try (PrintWriter out = response.getWriter();
                     Stream<String> lines = Files.lines(Paths.get(csvFilePath))) {
                    lines.forEach(out::println);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (NotEnoughMoneyException | AnyProblemsWithProductOrEnteringArgumentsException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (AnyOtherException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Map<Product, Integer> getShoppingCart(List<ProductForBayDTO> productsForBay)
            throws AnyProblemsWithProductOrEnteringArgumentsException, AnyOtherException {
        Map<Product, Integer> shoppingCart = new HashMap<>();

        for (ProductForBayDTO  productForBay : productsForBay) {
            Product product = productService.getProductById(productForBay.id());
            shoppingCart.put(product,
                    shoppingCart.getOrDefault(product, 0) + productForBay.quantity());
        }

        return shoppingCart;
    }

}
