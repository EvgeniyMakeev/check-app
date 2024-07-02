package ru.clevertec.check.dao.impl;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.model.DiscountCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DiscountCardDAOInMemory implements DAO<DiscountCard> {
    private final Map<Long, DiscountCard> discountCards = new HashMap<>();

    @Override
    public void add(DiscountCard newDiscountCard) {
        discountCards.put(newDiscountCard.id(), newDiscountCard);
    }

    @Override
    public Optional<DiscountCard> getById(long id) {
        return Optional.ofNullable(discountCards.get(id));
    }

    @Override
    public List<DiscountCard> getAll() {
        return discountCards.values().stream().toList();
    }
}
