package ru.clevertec.check.util;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager {

    Connection open() throws SQLException;
}