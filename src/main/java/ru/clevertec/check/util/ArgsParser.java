package ru.clevertec.check.util;

import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;

import java.util.HashMap;
import java.util.Map;

public class ArgsParser {

    private String resultFilePath = "";
    private String url = "";
    private String userName = "";
    private String password = "";

    private final Map<Long, Integer> shoppingCartArgs = new HashMap<>();
    private int discountCardNumber = -1;
    private double balanceDebitCard = 0.0;

    public void parseArguments(String[] args) throws AnyProblemsWithProductOrEnteringArgumentsException {
        for (String arg : args) {
            if (arg.startsWith("discountCard=")) {
                readDiscountCardNumber(arg);
            } else if (arg.startsWith("balanceDebitCard=")) {
                balanceDebitCard = Math.round(Double.parseDouble(arg.split("=")[1]));
            } else if (arg.startsWith("saveToFile=")) {
                resultFilePath = arg.split("=")[1];
            } else if (arg.startsWith("datasource.url=")) {
                url = arg.split("=")[1];
            } else if (arg.startsWith("datasource.username=")) {
                userName = arg.split("=")[1];
            } else if (arg.startsWith("datasource.password=")) {
                password = arg.split("=")[1];
            } else {
                argsProductsToChart(arg);
            }
        }
    }

    private void argsProductsToChart(String argsProductsToChart) {
        String[] productInfo = argsProductsToChart.split("-");
        long productId = Long.parseLong(productInfo[0]);
        int quantity = Integer.parseInt(productInfo[1]);
        shoppingCartArgs.put(productId,
                shoppingCartArgs.getOrDefault(productId, 0) + quantity);
    }

    private void readDiscountCardNumber(String arg) {
        String cardNumberFromArgs = arg.split("=")[1];
        if (cardNumberFromArgs.matches("\\d{4}")) {
            discountCardNumber = Integer.parseInt(cardNumberFromArgs);
        }
    }

    public String getResultFilePath() {
        return resultFilePath;
    }

    public int getDiscountCardNumber() {
        return discountCardNumber;
    }

    public double getBalanceDebitCard() {
        return balanceDebitCard;
    }

    public Map<Long, Integer> getShoppingCartArgs() {
        return shoppingCartArgs;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
