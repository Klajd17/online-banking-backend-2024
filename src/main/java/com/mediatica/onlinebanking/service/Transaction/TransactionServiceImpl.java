package com.mediatica.onlinebanking.service.Transaction;

import com.mediatica.onlinebanking.models.Transaction;

import java.util.List;

public interface TransactionServiceImpl {
    Transaction createTransaction(Transaction transaction);
    List<Transaction> getAllTransactions();
    Transaction getTransactionDetails(int id);
}
