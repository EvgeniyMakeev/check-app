package ru.clevertec.check.model;

public record Product(long id,
                      String description,
                      double price,
                      int quantityInStock,
                      boolean isWholesale) {
}
