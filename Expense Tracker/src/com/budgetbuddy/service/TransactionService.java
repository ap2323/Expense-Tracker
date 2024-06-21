package com.budgetbuddy.service;

import com.budgetbuddy.dao.ITransactionDAO;
import com.budgetbuddy.dao.TransactionDAOImpl;
import com.budgetbuddy.models.Transaction;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

public class TransactionService {

    private final ITransactionDAO transactionDAO;

    public TransactionService() {
        this.transactionDAO = TransactionDAOImpl.getInstance();
    }

    public List<Transaction> getRecentTransactions(int userId, YearMonth yearMonth, int limit) throws SQLException {
        return transactionDAO.getRecentTransactions(userId, yearMonth,limit);
    }

    public void removeTransaction(int transactionId) throws SQLException{
        transactionDAO.removeTransaction(transactionId);
    }

    public void addTransaction(Object object) throws SQLException {
        transactionDAO.addTransaction(object);
    }

    public void updateTransaction(Transaction newTransaction, Transaction oldTransaction) throws SQLException{
        transactionDAO.updateTransaction(newTransaction, oldTransaction);
    }

    public List<Transaction> getTransactions(int userId, YearMonth yearMonth) throws SQLException{
        return transactionDAO.getTransactions(userId, yearMonth);
    }

    public void delete(int userId) throws SQLException {
        transactionDAO.delete(userId);
    }
}

