package ru.clevertec.check.service;

import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.exception.NotEnoughMoneyException;
import ru.clevertec.check.model.Check;
import ru.clevertec.check.model.DiscountCard;
import ru.clevertec.check.model.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckService {

    private static final int MIN_WHOLESALE_QUANTITIES = 5;
    private static final double WHOLESALE_DISCOUNT = 0.10;

    private final String resultFilePath;

    private final List<String> purchasedProducts = new ArrayList<>();
    private double totalPrice = 0.0;
    private double totalDiscount = 0.0;

    public CheckService(String resultFilePath) {
        this.resultFilePath = resultFilePath;
    }


    public void makeCheck(DiscountCard discountCard, Map<Product, Integer> shoppingCart, double balanceDebitCard)
            throws IOException, AnyOtherException, NotEnoughMoneyException,
            AnyProblemsWithProductOrEnteringArgumentsException {


        double discountPercentage = discountCard.discountAmount() / 100.0;

        calculateCheck(shoppingCart, discountPercentage);

        if (totalPrice > balanceDebitCard) {
            throw new NotEnoughMoneyException();
        }

        Check check = new Check.CheckBuilder()
                .setNumberOfDiscountCard(discountCard.number())
                .setDiscountAmount(discountCard.discountAmount())
                .setTotalPrice(totalPrice)
                .setTotalDiscount(totalDiscount)
                .setPurchasedProducts(purchasedProducts)
                .build();

        saveToCSV(check.toString());
        printToConsole(check.toString());
    }

    private void calculateCheck(Map<Product, Integer> productQuantities, double discountPercentage) throws AnyProblemsWithProductOrEnteringArgumentsException {
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

    public void saveToCSV(String cvsResult) {
        try (FileWriter writer = new FileWriter(resultFilePath)) {
            writer.write(cvsResult);
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
        }
    }

    public void printToConsole(String cvsResult) {
        System.out.println(cvsResult);
    }
}
