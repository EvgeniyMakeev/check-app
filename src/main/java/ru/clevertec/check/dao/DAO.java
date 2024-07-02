package ru.clevertec.check.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    void add(T t);
    Optional<T> getById(long id);
    List<T> getAll();
}