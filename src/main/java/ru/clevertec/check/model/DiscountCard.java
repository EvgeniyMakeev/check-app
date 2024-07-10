package ru.clevertec.check.model;

public record DiscountCard(long id,
                           int number,
                           int discountAmount) {
    public DiscountCard(int number, int discountAmount) {
        this(-1L, number, discountAmount);
    }
}
