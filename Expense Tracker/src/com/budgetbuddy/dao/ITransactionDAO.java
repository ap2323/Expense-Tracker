package com.budgetbuddy.dao;

import com.budgetbuddy.models.Transaction;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

public interface ITransactionDAO {
    boolean addTransaction(Object object) throws SQLException;
    boolean updateTransaction(Transaction newTransaction, Transaction oldTransaction) throws SQLException;
    List<Transaction> getRecentTransactions(int userId, YearMonth yearMonth , int limit) throws SQLException;
    void removeTransaction(int transactionId) throws SQLException;
    List<Transaction> getTransactions(int userId, YearMonth yearMonth) throws SQLException;
    void delete(int userId) throws SQLException;
}
