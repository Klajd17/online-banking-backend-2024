package com.mediatica.onlinebanking.service.Account;

import com.mediatica.onlinebanking.dto.AccountDTO;
import com.mediatica.onlinebanking.models.Account;

import java.util.List;

public interface AccountServiceImpl {
    Account createAccount(Account account);
    Account updateAccount(int id, Account account);
    Account deleteAccount(int id);
    AccountDTO getAccountDetails(int id);
    List<AccountDTO> getAllAccounts();

}