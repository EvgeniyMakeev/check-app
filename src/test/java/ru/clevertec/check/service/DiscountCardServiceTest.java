package ru.clevertec.check.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.model.DiscountCard;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscountCardServiceTest {

    @Mock
    private DiscountCard mockDiscountCard;

    @Mock
    private DAO<DiscountCard, Integer> discountCardDao;

    @InjectMocks
    private DiscountCardService discountCardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDiscountCardByNumber_shouldReturnDiscountCard() throws AnyOtherException {
        when(discountCardDao.getBy(1111)).thenReturn(Optional.of(mockDiscountCard));

        DiscountCard result = discountCardService.getDiscountCardByNumber(1111);

        assertThat(result).isEqualTo(mockDiscountCard);
        verify(discountCardDao, times(1)).getBy(1111);
        assertDoesNotThrow(() -> discountCardService.getDiscountCardByNumber(1111));
    }

    @Test
    void getDiscountCardByNumber_shouldReturnBasicDiscountCard() throws AnyOtherException {
        int baseDiscount = 2;
        when(discountCardDao.getBy(9999)).thenReturn(Optional.empty());
        when(mockDiscountCard.number()).thenReturn(9999);
        when(mockDiscountCard.discountAmount()).thenReturn(baseDiscount);

        DiscountCard result = discountCardService.getDiscountCardByNumber(9999);

        assertThat(result.number()).isEqualTo(9999);
        assertThat(result.discountAmount()).isEqualTo(baseDiscount);
        verify(discountCardDao, times(1)).getBy(9999);
    }

}