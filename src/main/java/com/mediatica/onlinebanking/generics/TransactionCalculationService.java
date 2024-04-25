package com.mediatica.onlinebanking.generics;

import com.mediatica.onlinebanking.enums.CurrencyType;
import com.mediatica.onlinebanking.enums.TransactionType;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.models.Transaction;
import com.mediatica.onlinebanking.service.Account.AccountService;
import com.mediatica.onlinebanking.service.Transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionCalculationService<T extends Transaction>{
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    TransactionCalculationService(AccountService accountService, TransactionService transactionService)
    {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public BigDecimal getTotalAmmount(List<T> incomingTransactions)
    {

        BigDecimal transferredAmount = new BigDecimal("0");

        for(T transaction: incomingTransactions)
        {
            CurrencyType transactionCurrency = transaction.getCurrency();

            if(transaction.getTransactionType() == TransactionType.DEPOSIT)
                transferredAmount.add(transaction.getAmount());

            else if(transaction.getTransactionType() == TransactionType.TRANSFER)
            {
                CurrencyType receivingAccountCurrency = accountService.getAccountDetails(transaction.getToAccountId()).getCurrency();

                if(transactionCurrency != receivingAccountCurrency)
                {
                    BigDecimal convertedIncomingAmount = transaction.getAmount().multiply(transaction.getToAccountConversionRate());
                    transferredAmount.add(convertedIncomingAmount);
                }
            }
        }

        return transferredAmount;
    }

}
