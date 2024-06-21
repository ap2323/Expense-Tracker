package com.budgetbuddy.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.YearMonth;
import java.util.List;

public interface AbstractDAO<T> {
    boolean add(T object) throws SQLException;
    float get(int userId, YearMonth yearMonth) throws SQLException;
    void remove(int id) throws SQLException;
    void update(int id, T newObject) throws SQLException;
    int getId(int userId, int categoryId, float amount, String description, Date date, Time time) throws SQLException;
    void delete(int userId) throws SQLException;
    List<T> getByMonth(int userId, YearMonth yearMonth) throws SQLException;
}
