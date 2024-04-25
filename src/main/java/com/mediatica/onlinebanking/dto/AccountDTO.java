package com.mediatica.onlinebanking.dto;

import com.mediatica.onlinebanking.dateTime.FirstLastDayOfMonth;
import com.mediatica.onlinebanking.enums.CurrencyType;
import com.mediatica.onlinebanking.enums.TransactionType;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.models.Transaction;
import com.mediatica.onlinebanking.repository.AccountRepository;
import com.mediatica.onlinebanking.repository.TransactionRepository;
import com.mediatica.onlinebanking.repository.UserRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
public class AccountDTO {

    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private CurrencyType currency;
    private String accountType;

    private BigDecimal accountTotalMonthlyIncoming;
    private BigDecimal accountTotalMonthlyOutcoming;
    private List<Transaction> trasactionsList;

    public AccountDTO(Account account){

        setAccountId(account.getAccountId());
        setUserId(account.getUserId());
        setAccountNumber(account.getAccountNumber());
        setAccountName(account.getAccountName());
        setAccountType(account.getAccountType());
        setBalance(account.getBalance());
        setCurrency(account.getCurrency());
    }

}
