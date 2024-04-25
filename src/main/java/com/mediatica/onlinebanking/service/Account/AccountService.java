package com.mediatica.onlinebanking.service.Account;

import com.mediatica.onlinebanking.dto.AccountDTO;
import com.mediatica.onlinebanking.enums.CurrencyType;
import com.mediatica.onlinebanking.enums.TransactionType;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.models.Transaction;
import com.mediatica.onlinebanking.repository.AccountRepository;
import com.mediatica.onlinebanking.generics.*;
import com.mediatica.onlinebanking.dateTime.TransactionTimestamp;
import com.mediatica.onlinebanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


@Service
public class AccountService implements AccountServiceImpl {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public List<AccountDTO> getAllAccounts() {

        List<Account> accountList = accountRepository.findAll();

        List <AccountDTO> accountDTOList = accountList.stream()
                .map(AccountDTO::new)
                .toList();

        for(AccountDTO accountDTO: accountDTOList)
        {
            int accountId = accountDTO.getAccountId();
            accountDTO.setAccountTotalMonthlyIncoming(calculateAccountMonthlyIncoming(accountId));
            accountDTO.setAccountTotalMonthlyOutcoming(calculateAccountMonthlyOutcoming(accountId));
            accountDTO.setTrasactionsList(getAllAccountTransactions(accountId));
        }

        return accountDTOList;
    }

    @Override
    public AccountDTO getAccountDetails(int id)
    {
        Account existingAccount = accountRepository.findByAccountId(id);

        if(existingAccount != null) {

            AccountDTO accountDTO = new AccountDTO(existingAccount);

            accountDTO.setAccountTotalMonthlyIncoming(calculateAccountMonthlyIncoming(id));
            accountDTO.setAccountTotalMonthlyOutcoming(calculateAccountMonthlyOutcoming(id));
            accountDTO.setTrasactionsList(getAllAccountTransactions(id));

            return accountDTO;
        }

        else
            return null;
    }

    @Override
    public Account updateAccount(int id, Account account) {

        Account existingAccount = accountRepository.findByAccountId(id);

        if(existingAccount != null)
        {
            RecordUpdate<Account, Account, AccountRepository> genericClassInstance = new RecordUpdate<Account, Account, AccountRepository>(existingAccount, account, accountRepository);
            genericClassInstance.updateRecord();
            existingAccount.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // Save the updated 'existingAccount'
            Account updatedAccount = accountRepository.save(existingAccount);
            if(updatedAccount != null)
                return updatedAccount;
            else
                return null;

        }

        else
            return null; // Handle the situation when the account is not found
    }


    @Override
    public Account deleteAccount(int id) {

        Account existingAccount = accountRepository.findByAccountId(id);

        if (existingAccount != null)
        {
            accountRepository.delete(existingAccount);
            return existingAccount;
        }

        else
            return null;
    }

    public BigDecimal calculateAccountMonthlyIncoming(int accountId)
    {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // This section will calculate the total incoming in all the authenticated user's accounts.
        List<Transaction> incomingTransactions = transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.getToAccountId() == accountId && (transaction.getTransactionType()== TransactionType.DEPOSIT || transaction.getTransactionType()==TransactionType.TRANSFER) && new TransactionTimestamp().transactionOccurredWithinCurrentMonth(transaction, currentTimestamp))
                .toList();


        return incomingTransactions.stream()
                .map(transaction -> {

                    BigDecimal amount = transaction.getAmount();
                    CurrencyType transactionCurrency = transaction.getCurrency();
                    CurrencyType receivingAccountCurrency = accountRepository.findByAccountId(transaction.getToAccountId()).getCurrency();

                    if(transaction.getTransactionType() == TransactionType.DEPOSIT)
                        return amount;

                    else if(transaction.getTransactionType() == TransactionType.TRANSFER) {

                        if(transactionCurrency != receivingAccountCurrency)
                        {
                            BigDecimal convertedToReceivingAccountCurrencyAmount =  amount.multiply(transaction.getToAccountConversionRate());
                            CurrencyType receivingAccountUserDefaultCurrency = accountRepository.findByAccountId(transaction.getToAccountId()).getUser().getDefaultCurrency();

                            if(receivingAccountCurrency != receivingAccountUserDefaultCurrency)
                                return convertedToReceivingAccountCurrencyAmount.multiply(transaction.getToAccountDefaultCurrencyConversionRate());
                            else
                                return convertedToReceivingAccountCurrencyAmount;
                        }

                        else
                            return amount;

                    }
                    else
                        return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal calculateAccountMonthlyOutcoming(int accountId)
    {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // This section will calculate the total outcoming amount from all the authenticated user's accounts.
        List<Transaction> outcomingTransactions = transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.getFromAccountId() == accountId && (transaction.getTransactionType()==TransactionType.WITHDRAWAL || transaction.getTransactionType()==TransactionType.TRANSFER) && new TransactionTimestamp().transactionOccurredWithinCurrentMonth(transaction, currentTimestamp))
                .toList();


        return outcomingTransactions.stream()
                .map(transaction -> {

                    BigDecimal amount = transaction.getAmount();
                    CurrencyType transactionCurrency = transaction.getCurrency();

                    if(transaction.getTransactionType() == TransactionType.WITHDRAWAL)
                        return amount;

                    else if(transaction.getTransactionType() == TransactionType.TRANSFER)
                    {
                        CurrencyType transferringAccountCurrency = accountRepository.findByAccountId(transaction.getFromAccountId()).getCurrency();

                        if(transactionCurrency != transferringAccountCurrency)
                            return amount.multiply(transaction.getFromAccountConversionRate());

                        else
                            return amount;

                    }

                    else
                        return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transaction> getAllAccountTransactions(int accountId)
    {
        return transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.getFromAccountId() == accountId || transaction.getToAccountId() == accountId)
                .toList();

    }

}