package com.mediatica.onlinebanking.repository;

import com.mediatica.onlinebanking.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByFromAccountId(int accountId);
    List<Transaction> findByToAccountId(int accountId);
    List<Transaction> findAll();
    Transaction findByTransactionId(int transactionId);
}