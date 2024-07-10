package ru.clevertec.check.service;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.DiscountCard;

public class DiscountCardService {
    private final DAO<DiscountCard, Integer> discountCardDAO;

    private static final int BASE_DISCOUNT_AMOUNT = 2;

    public DiscountCardService(DAO<DiscountCard, Integer> discountCardDAO) {
        this.discountCardDAO = discountCardDAO;
    }

    public DiscountCard getDiscountCardByNumber(int cardNumber) throws AnyOtherException {
        return discountCardDAO.getBy(cardNumber).orElse(new DiscountCard(cardNumber, BASE_DISCOUNT_AMOUNT));
    }
}
