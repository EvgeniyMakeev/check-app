package ru.clevertec.check;

import ru.clevertec.check.dao.DAOFactory;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.exception.NotEnoughMoneyException;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.service.CheckService;
import ru.clevertec.check.service.DiscountCardService;
import ru.clevertec.check.service.ProductService;
import ru.clevertec.check.util.ArgsParser;
import ru.clevertec.check.util.ConnectionManager;
import ru.clevertec.check.util.impl.ConnectionManagerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class CheckRunner {

    private static final ArgsParser ARGS_PARSER = new ArgsParser();

    private static ProductService productService;
    private static DiscountCardService discountCardService;
    private static CheckService checkService;

    public static void main(String[] args) {
        try {
            ARGS_PARSER.parseArguments(args);

            initCheckService();

            checkService.makeCheck(
                    discountCardService.getDiscountCardByNumber(ARGS_PARSER.getDiscountCardNumber()),
                    getShoppingCart(ARGS_PARSER.getShoppingCartArgs()),
                    ARGS_PARSER.getBalanceDebitCard());

        } catch (AnyProblemsWithProductOrEnteringArgumentsException
                 | AnyOtherException
                 | NotEnoughMoneyException
                 | IOException e) {
            checkService.saveToCSV(e.getMessage());
            checkService.printToConsole(e.getMessage());
        }
    }

    private static void initCheckService() throws AnyProblemsWithProductOrEnteringArgumentsException {
        String resultFilePath = ARGS_PARSER.getResultFilePath();
        String url = ARGS_PARSER.getUrl();
        String userName = ARGS_PARSER.getUserName();
        String password = ARGS_PARSER.getPassword();

        if (resultFilePath.isBlank()) {
            resultFilePath = "result.csv";
            checkService = new CheckService(resultFilePath);
            throw new AnyProblemsWithProductOrEnteringArgumentsException();
        } else if (url.isBlank() || userName.isBlank() || password.isBlank()) {
            checkService = new CheckService(resultFilePath);
            throw new AnyProblemsWithProductOrEnteringArgumentsException();
        } else {
            ConnectionManager connectionManager = new ConnectionManagerImpl(url, userName, password);
            productService = new ProductService(DAOFactory.createProductDAO(connectionManager));
            discountCardService = new DiscountCardService(DAOFactory.createDiscountCardDAO(connectionManager));
            checkService = new CheckService(resultFilePath);
        }
    }

    private static Map<Product, Integer> getShoppingCart(Map<Long, Integer> shoppingCartArgs)
            throws AnyProblemsWithProductOrEnteringArgumentsException, AnyOtherException {
        Map<Product, Integer> shoppingCart = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : shoppingCartArgs.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            Product product = productService.getProductById(productId);
            shoppingCart.put(product,
                    shoppingCart.getOrDefault(product, 0) + quantity);
        }

        return shoppingCart;
    }

}

