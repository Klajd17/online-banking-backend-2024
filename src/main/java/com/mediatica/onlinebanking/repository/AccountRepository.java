package com.mediatica.onlinebanking.repository;

import com.mediatica.onlinebanking.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AccountRepository extends JpaRepository<Account,Integer> {
    Account findByAccountNumber(String accountNumber);
    Account findByAccountId(int accountId);
    List<Account> findAll();
}
