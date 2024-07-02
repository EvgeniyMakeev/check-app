package ru.clevertec.check.service;

import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.model.DiscountCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class DiscountCardService {
    private final DAO<DiscountCard> discountCardDAO;

    public DiscountCardService(DAO<DiscountCard> discountCardDAO) {
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
        return discountCardDAO.getAll().stream()
                .filter(card -> card.number().equalsIgnoreCase(cardNumber))
                .findFirst();
    }
}
