package ru.clevertec.check;

import ru.clevertec.check.dao.DAOFactory;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.exception.NotEnoughMoneyException;
import ru.clevertec.check.model.Check;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.service.DiscountCardService;
import ru.clevertec.check.service.ProductService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class CheckRunner {
    private static final ProductService PRODUCT_SERVICE = new ProductService(DAOFactory.createProductDAO());
    private static final DiscountCardService DISCOUNT_CARD_SERVICE = new DiscountCardService(DAOFactory.createDiscountCardDAO());

    private static final String PRODUCTS_FILE_PATH = "src/main/resources/products.csv";
    private static final String DISCOUNT_CARDS_FILE_PATH = "src/main/resources/discountCards.csv";
    private static final String RESULT_FILE_PATH = "result.csv";

    private static final int MIN_WHOLESALE_QUANTITIES = 5;
    private static final double WHOLESALE_DISCOUNT = 0.10;
    private static final int BASE_DISCOUNT_AMOUNT = 2;

    private static String discountCardNumber = "";
    private static final Map<Product, Integer> productQuantities = new HashMap<>();
    private static double balance = 0.0;
    private static int discountAmount = BASE_DISCOUNT_AMOUNT;
    private static final double discountPercentage =  discountAmount / 100.0;
    private static final List<String> purchasedProducts = new ArrayList<>();
    private static double totalPrice = 0.0;
    private static double totalDiscount = 0.0;

    public static void main(String[] args) {
        try {
            PRODUCT_SERVICE.readProductsFromCSV(PRODUCTS_FILE_PATH);
            DISCOUNT_CARD_SERVICE.readDiscountCardsFromCSV(DISCOUNT_CARDS_FILE_PATH);
            makeCheck(args);
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        } catch (AnyOtherException |
                 NotEnoughMoneyException |
                 AnyProblemsWithProductOrEnteringArgumentsException e) {
            saveToCSV(e.getMessage());
            printToConsole(e.getMessage());
        }
    }

    private static void makeCheck(String[] args)
            throws IOException, AnyOtherException, NotEnoughMoneyException,
            AnyProblemsWithProductOrEnteringArgumentsException {

        parseArguments(args);
        calculateCheck();

        if (totalPrice > balance) {
            throw new NotEnoughMoneyException();
        }

        Check check = new Check.CheckBuilder()
                .setNumberOfDiscountCard(discountCardNumber)
                .setDiscountAmount(discountAmount)
                .setTotalPrice(totalPrice)
                .setTotalDiscount(totalDiscount)
                .setPurchasedProducts(purchasedProducts)
                .build();

        saveToCSV(check.toString());
        printToConsole(check.toString());
    }

    private static void parseArguments(String[] args) throws AnyProblemsWithProductOrEnteringArgumentsException {
        for (String arg : args) {
            if (arg.startsWith("discountCard=")) {
                readDiscountCardNumber(arg);
            } else if (arg.startsWith("balanceDebitCard=")) {
                balance = Double.parseDouble(arg.split("=")[1]);
            } else {
                readProductsToBay(arg);
            }
        }
    }

    private static void readProductsToBay(String arg) throws AnyProblemsWithProductOrEnteringArgumentsException {
        String[] productInfo = arg.split("-");
        long productId = Long.parseLong(productInfo[0]);
        int quantity = Integer.parseInt(productInfo[1]);
        Product product = PRODUCT_SERVICE.getProductById(productId);
        productQuantities.put(product,
                productQuantities.getOrDefault(product, 0) + quantity);
    }

    private static void readDiscountCardNumber(String arg) {
        String cardNumberFromArgs = arg.split("=")[1];
        if (cardNumberFromArgs.matches("\\d{4}")) {
            discountCardNumber = cardNumberFromArgs;
            Optional<DiscountCard> discountCard = DISCOUNT_CARD_SERVICE.getDiscountCardByNumber(discountCardNumber);
            discountCard.ifPresent(card -> discountAmount = card.discountAmount());
        }
    }

    private static void calculateCheck() throws AnyProblemsWithProductOrEnteringArgumentsException {
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            if (quantity > product.quantityInStock()) {
                throw new AnyProblemsWithProductOrEnteringArgumentsException();
            }

            double total = product.price() * quantity;
            double discount = product.isWholesale() && quantity >= MIN_WHOLESALE_QUANTITIES ? total * WHOLESALE_DISCOUNT : total * discountPercentage;

            totalPrice += total;
            totalDiscount += discount;

            purchasedProducts.add(String.format("%d;%s;%.2f$;%.2f$;%.2f$\n",
                    quantity, product.description(), product.price(), discount, total));
        }
    }

    private static void saveToCSV(String cvsResult) {
        try (FileWriter writer = new FileWriter(RESULT_FILE_PATH)) {
            writer.write(cvsResult);
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
        }
    }

    private static void printToConsole(String cvsResult) {
        System.out.println(cvsResult);
    }
}
