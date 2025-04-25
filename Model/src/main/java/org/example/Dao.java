package org.example;

import java.sql.Connection;
import java.util.List;

public interface Dao<T> extends AutoCloseable {
    <T> T write(T obj);
    <T> List<T> read();
    Connection connect();
}
