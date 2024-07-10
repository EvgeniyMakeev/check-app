package ru.clevertec.check.util;

import java.sql.Connection;

public interface ConnectionManager {

    Connection open();
}