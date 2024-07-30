package ru.clevertec.check.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.clevertec.check.dao.DAO;
import ru.clevertec.check.exception.AnyOtherException;
import ru.clevertec.check.exception.AnyProblemsWithProductOrEnteringArgumentsException;
import ru.clevertec.check.model.DiscountCard;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscountCardServiceTest {

    @Mock
    private DiscountCard mockDiscountCard;

    @Mock
    private DAO<DiscountCard> discountCardDao;

    @InjectMocks
    private DiscountCardService discountCardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDiscountCardByNumber_shouldReturnDiscountCard() throws AnyOtherException {
        when(discountCardDao.getAll()).thenReturn(List.of(mockDiscountCard));
        when(mockDiscountCard.number()).thenReturn(1111);

        DiscountCard result = discountCardService.getDiscountCardByNumber(1111);

        assertThat(result.number()).isEqualTo(mockDiscountCard.number());
        verify(discountCardDao, times(1)).getAll();
        assertDoesNotThrow(() -> discountCardService.getDiscountCardByNumber(1111));
    }

    @Test
    void getDiscountCardByNumber_shouldReturnBasicDiscountCard() throws AnyOtherException {
        int baseDiscount = 2;
        when(discountCardDao.getAll()).thenReturn(List.of());
        when(mockDiscountCard.number()).thenReturn(9999);
        when(mockDiscountCard.discountAmount()).thenReturn(baseDiscount);

        DiscountCard result = discountCardService.getDiscountCardByNumber(9999);

        assertThat(result.number()).isEqualTo(9999);
        assertThat(result.discountAmount()).isEqualTo(baseDiscount);
        verify(discountCardDao, times(1)).getAll();
    }

    @Test
    void addDiscountCard_shouldAddDiscountCard() throws AnyOtherException {
        DiscountCard newDiscountCard = new DiscountCard(1L, 2222, 10);
        doNothing().when(discountCardDao).add(newDiscountCard);

        discountCardService.addDiscountCard(newDiscountCard);

        verify(discountCardDao, times(1)).add(newDiscountCard);
    }

    @Test
    void getDiscountCardById_shouldReturnDiscountCard() throws AnyOtherException, AnyProblemsWithProductOrEnteringArgumentsException {
        when(discountCardDao.getBy(1L)).thenReturn(Optional.of(mockDiscountCard));
        when(mockDiscountCard.number()).thenReturn(1111);
        when(mockDiscountCard.discountAmount()).thenReturn(10);

        DiscountCard result = discountCardService.getDiscountCardById(1L);

        assertThat(result.number()).isEqualTo(1111);
        assertThat(result.discountAmount()).isEqualTo(10);
        verify(discountCardDao, times(1)).getBy(1L);
    }

    @Test
    void getDiscountCardById_shouldThrowExceptionWhenNotFound() throws AnyOtherException {
        when(discountCardDao.getBy(1L)).thenReturn(Optional.empty());

        assertThrows(AnyProblemsWithProductOrEnteringArgumentsException.class, () -> discountCardService.getDiscountCardById(1L));
        verify(discountCardDao, times(1)).getBy(1L);
    }

    @Test
    void updateDiscountCard_shouldUpdateDiscountCard() throws AnyOtherException {
        DiscountCard updatedDiscountCard = new DiscountCard(1L, 2222, 15);
        doNothing().when(discountCardDao).update(updatedDiscountCard);

        discountCardService.updateDiscountCard(1L, updatedDiscountCard);

        verify(discountCardDao, times(1)).update(updatedDiscountCard);
    }

    @Test
    void deleteDiscountCard_shouldDeleteDiscountCard() throws AnyOtherException {
        doNothing().when(discountCardDao).delete(1L);

        discountCardService.deleteDiscountCard(1L);

        verify(discountCardDao, times(1)).delete(1L);
    }
}
