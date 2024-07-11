package ru.clevertec.check.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PurchaseDTO(List<ProductForBayDTO> products,
                          @JsonProperty("discountCard")
                          int discountCardNumber,
                          double balanceDebitCard) {
}
