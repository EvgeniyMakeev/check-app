package ru.clevertec.check;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class CheckRunner {

    private static final String DISCOUNT_CARDS_FILE_PATH = "src/main/resources/discountCards.csv";
    private static final int BASE_DISCOUNT_AMOUNT = 2;

    private static String productsFilePath = "";
    private static String resultFilePath = "";

    private static final Map<Product, Integer> shoppingCart = new HashMap<>();
    private static String argsProductsToChart = "";
    private static String discountCardNumber = "";
    private static double balanceDebitCard = 0.0;

    private static final ProductService PRODUCT_SERVICE = new ProductService(DAOFactory.createProductDAO());
    private static final DiscountCardService DISCOUNT_CARD_SERVICE = new DiscountCardService(DAOFactory.createDiscountCardDAO());
    private static CheckService checkService;

    public static void main(String[] args) {
        try {
            DISCOUNT_CARD_SERVICE.readDiscountCardsFromCSV(DISCOUNT_CARDS_FILE_PATH);

            parseArguments(args);
            checkingAgrs();

            checkService = new CheckService(resultFilePath);

            PRODUCT_SERVICE.readProductsFromCSV(productsFilePath);
            readProductsToBay(argsProductsToChart);

            DiscountCard discountCard =
                    DISCOUNT_CARD_SERVICE.getDiscountCardByNumber(discountCardNumber)
                            .orElse(new DiscountCard(discountCardNumber, BASE_DISCOUNT_AMOUNT));

            checkService.makeCheck(discountCard, shoppingCart, balanceDebitCard);
        } catch (AnyProblemsWithProductOrEnteringArgumentsException
                 | AnyOtherException
                 | NotEnoughMoneyException
                 | IOException e) {
            checkService.saveToCSV(e.getMessage());
            checkService.printToConsole(e.getMessage());
        }
    }

    private static void parseArguments(String[] args) throws AnyProblemsWithProductOrEnteringArgumentsException {
        for (String arg : args) {
            if (arg.startsWith("discountCard=")) {
                readDiscountCardNumber(arg);
            } else if (arg.startsWith("balanceDebitCard=")) {
                balanceDebitCard = Math.round(Double.parseDouble(arg.split("=")[1]));
            } else if (arg.startsWith("pathToFile=")) {
                productsFilePath = arg.split("=")[1];
            } else if (arg.startsWith("saveToFile=")) {
                resultFilePath = arg.split("=")[1];
            } else {
                argsProductsToChart = arg;
            }
        }
    }

    private static void checkingAgrs() throws AnyProblemsWithProductOrEnteringArgumentsException {
        if (productsFilePath.isBlank() || resultFilePath.isBlank()) {
            resultFilePath = "result.csv";
            checkService = new CheckService(resultFilePath);
            throw new AnyProblemsWithProductOrEnteringArgumentsException();
        }
    }

    private static void readProductsToBay(String argsProductsToChart) throws AnyProblemsWithProductOrEnteringArgumentsException {
        String[] productInfo = argsProductsToChart.split("-");
        long productId = Long.parseLong(productInfo[0]);
        int quantity = Integer.parseInt(productInfo[1]);
        Product product = PRODUCT_SERVICE.getProductById(productId);
        shoppingCart.put(product,
                shoppingCart.getOrDefault(product, 0) + quantity);
    }

    private static void readDiscountCardNumber(String arg) {
        String cardNumberFromArgs = arg.split("=")[1];
        if (cardNumberFromArgs.matches("\\d{4}")) {
            discountCardNumber = cardNumberFromArgs;
        }
    }
}

class ProductService {
    private final DAO<Product, Long> productDao;

    public ProductService(DAO<Product, Long> productDao) {
        this.productDao = productDao;
    }

    public void readProductsFromCSV(String productsFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(productsFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                long id = Long.parseLong(values[0]);
                String description = values[1];
                double price = Math.round(Double.parseDouble(values[2]) * 100.0) / 100.0;
                int quantityInStock = Integer.parseInt(values[3]);
                boolean wholesaleProduct = values[4].equals("+");
                productDao.add(new Product(id, description, price, quantityInStock, wholesaleProduct));
            }
        }
    }

    public Product getProductById(long productId) throws AnyProblemsWithProductOrEnteringArgumentsException {
        return productDao.getBy(productId).orElseThrow(AnyProblemsWithProductOrEnteringArgumentsException::new);
    }
}

class DiscountCardService {
    private final DAO<DiscountCard, String> discountCardDAO;

    public DiscountCardService(DAO<DiscountCard, String> discountCardDAO) {
        this.discountCardDAO = discountCardDAO;

    }

    public void readDiscountCardsFromCSV(String discountCardsFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(discountCardsFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                long id = Long.parseLong(values[0]);
                String number = values[1];
                int discountAmount = Integer.parseInt(values[2]);
                discountCardDAO.add(new DiscountCard(id, number, discountAmount));
            }
        }
    }

    public Optional<DiscountCard> getDiscountCardByNumber(String cardNumber) {
        return discountCardDAO.getBy(cardNumber);
    }
}

class CheckService {

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

interface DAO<T,V> {
    void add(T t);
    Optional<T> getBy(V v);
}

class DAOFactory {
    public static DAO<Product, Long> createProductDAO() {
        return new ProductDAOInMemory();
    }

    public static DAO<DiscountCard, String> createDiscountCardDAO() {
        return new DiscountCardDAOInMemory();
    }
}

class ProductDAOInMemory implements DAO<Product, Long> {
    private final Map<Long, Product> products = new HashMap<>();

    @Override
    public void add(Product newProduct) {
        products.put(newProduct.id(), newProduct);
    }

    @Override
    public Optional<Product> getBy(Long id) {
        return Optional.ofNullable(products.get(id));
    }
}

class DiscountCardDAOInMemory implements DAO<DiscountCard, String> {
    private final Map<String, DiscountCard> discountCards = new HashMap<>();

    @Override
    public void add(DiscountCard newDiscountCard) {
        discountCards.put(newDiscountCard.number(), newDiscountCard);
    }

    @Override
    public Optional<DiscountCard> getBy(String discountCardNumber) {
        return Optional.ofNullable(discountCards.get(discountCardNumber));
    }
}

record Product(long id,
               String description,
               double price,
               int quantityInStock,
               boolean isWholesale) {
}

record DiscountCard(long id,
                    String number,
                    int discountAmount) {
    public DiscountCard(String number, int discountAmount) {
        this(-1L, number, discountAmount);
    }
}

record Check(String numberOfDiscountCard,
             int discountAmount,
             double totalPrice,
             double totalDiscount,
             List<String> purchasedProducts) {

    @Override
    public String toString() {
        StringBuilder stringBuilderCSV = new StringBuilder();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        stringBuilderCSV.append("Date;Time\n")
                .append(dateFormatter.format(now))
                .append(";")
                .append(timeFormatter.format(now))
                .append("\n\nQTY;DESCRIPTION;PRICE;DISCOUNT;TOTAL\n");

        purchasedProducts.forEach(stringBuilderCSV::append);

        if (!numberOfDiscountCard.isBlank()) {
            stringBuilderCSV.append("\nDISCOUNT CARD;DISCOUNT PERCENTAGE\n")
                    .append(numberOfDiscountCard)
                    .append(";")
                    .append(discountAmount)
                    .append("\n");
        }

        stringBuilderCSV.append("\nTOTAL PRICE;TOTAL DISCOUNT;TOTAL WITH DISCOUNT\n")
                .append(String.format("%.2f$", totalPrice))
                .append(";")
                .append(String.format("%.2f$", totalDiscount))
                .append(";")
                .append(String.format("%.2f$", totalPrice - totalDiscount));

        return stringBuilderCSV.toString();
    }

    static final class CheckBuilder {
        private String numberOfDiscountCard;
        private int discountAmount;
        private double totalPrice;
        private double totalDiscount;
        private List<String> purchasedProducts;

        public CheckBuilder setNumberOfDiscountCard(String numberOfDiscountCard) {
            this.numberOfDiscountCard = numberOfDiscountCard;
            return this;
        }

        public CheckBuilder setDiscountAmount(int discountPercentage) {
            this.discountAmount = discountPercentage;
            return this;
        }

        public CheckBuilder setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public CheckBuilder setTotalDiscount(double totalDiscount) {
            this.totalDiscount = totalDiscount;
            return this;
        }

        public CheckBuilder setPurchasedProducts(List<String> purchasedProducts) {
            this.purchasedProducts = purchasedProducts;
            return this;
        }

        public Check build() {
            return new Check(numberOfDiscountCard, discountAmount, totalPrice, totalDiscount, purchasedProducts);
        }
    }
}

class AnyProblemsWithProductOrEnteringArgumentsException extends Exception {
    @Override
    public String getMessage() {
        return """
                ERROR
                BAD REQUEST""";
    }
}

class NotEnoughMoneyException extends Exception {
    @Override
    public String getMessage() {
        return """
                ERROR
                NOT ENOUGH MONEY""";
    }
}

class AnyOtherException extends Exception {
    @Override
    public String getMessage() {
        return """
                ERROR
                INTERNAL SERVER ERROR""";
    }
}

