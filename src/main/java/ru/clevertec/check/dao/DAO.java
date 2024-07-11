package ru.clevertec.check.dao;

import ru.clevertec.check.exception.AnyOtherException;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    void add(T t) throws AnyOtherException;
    Optional<T> getBy(long id) throws AnyOtherException;
    List<T> getAll() throws AnyOtherException;
    void update(T product) throws AnyOtherException;
    void delete(long id) throws AnyOtherException;
}
