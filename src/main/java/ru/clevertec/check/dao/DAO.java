package ru.clevertec.check.dao;

import ru.clevertec.check.exception.AnyOtherException;

import java.util.Optional;

public interface DAO<T, V> {
    Optional<T> getBy(V v) throws AnyOtherException;
}
