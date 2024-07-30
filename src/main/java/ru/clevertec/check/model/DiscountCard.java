package ru.clevertec.check.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DiscountCard(long id,
                           int number,
                           int discountAmount) {
    public DiscountCard(@JsonProperty("discountCard") int number,  @JsonProperty("discountAmount") int discountAmount) {
        this(-1L, number, discountAmount);
    }
}
