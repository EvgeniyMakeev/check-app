package ru.clevertec.check.service;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.DiscountCard;

import java.util.Optional;

public class DiscountCardService {
    private final DAO<DiscountCard> discountCardDAO;

    private static final int BASE_DISCOUNT_AMOUNT = 2;

    public DiscountCardService(DAO<DiscountCard> discountCardDAO) {
        this.discountCardDAO = discountCardDAO;
    }

    public void addDiscountCard(DiscountCard discountCard) throws AnyOtherException {
        discountCardDAO.add(discountCard);
    }

    public DiscountCard getDiscountCardByNumber(int cardNumber) throws AnyOtherException {
        Optional<DiscountCard> discountCard = discountCardDAO.getAll().stream().filter(card -> card.number() == cardNumber).findAny();
        return discountCard.orElse(new DiscountCard(cardNumber, BASE_DISCOUNT_AMOUNT));
    }

    public DiscountCard getDiscountCardById(long id) throws AnyOtherException, AnyProblemsWithProductOrEnteringArgumentsException {
        return discountCardDAO.getBy(id).orElseThrow(AnyProblemsWithProductOrEnteringArgumentsException::new);
    }

    public void updateDiscountCard(long id, DiscountCard discountCard) throws AnyOtherException {
        discountCardDAO.update(new DiscountCard(id, discountCard.number(), discountCard.discountAmount()));
    }

    public void deleteDiscountCard(long id) throws AnyOtherException {
        discountCardDAO.delete(id);
    }
}
