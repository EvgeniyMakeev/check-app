package ru.clevertec.check.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Product(long id,
                      String description,
                      double price,
                      int quantityInStock,
                      boolean isWholesale) {

    @JsonCreator
    public Product(@JsonProperty("description") String description,
                   @JsonProperty("price") double price,
                   @JsonProperty("quantity") int quantityInStock,
                   @JsonProperty("isWholesale") boolean isWholesale) {
        this(-1, description, price, quantityInStock, isWholesale);
    }
}
