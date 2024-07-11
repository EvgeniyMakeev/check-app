package ru.clevertec.check.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record Check(int numberOfDiscountCard,
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

        if (numberOfDiscountCard != 0) {
            stringBuilderCSV.append("\nDISCOUNT CARD;DISCOUNT PERCENTAGE\n")
                    .append(String.format("%04d", numberOfDiscountCard))
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

    public static final class CheckBuilder {
        private int numberOfDiscountCard;
        private int discountAmount;
        private double totalPrice;
        private double totalDiscount;
        private List<String> purchasedProducts;

        public CheckBuilder setNumberOfDiscountCard(int numberOfDiscountCard) {
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