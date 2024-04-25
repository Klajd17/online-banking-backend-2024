package com.mediatica.onlinebanking.service.Dashboard;

import com.mediatica.onlinebanking.currencyAPI.APIRequest;
import com.mediatica.onlinebanking.dateTime.FirstLastDayOfMonth;
import com.mediatica.onlinebanking.dto.AccountDTO;
import com.mediatica.onlinebanking.dto.DashboardDTO;
import com.mediatica.onlinebanking.enums.CurrencyType;
import com.mediatica.onlinebanking.enums.TransactionType;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.models.Card;
import com.mediatica.onlinebanking.models.Transaction;
import com.mediatica.onlinebanking.models.User;
import com.mediatica.onlinebanking.repository.AccountRepository;
import com.mediatica.onlinebanking.repository.CardRepository;
import com.mediatica.onlinebanking.repository.TransactionRepository;
import com.mediatica.onlinebanking.repository.UserRepository;
import com.mediatica.onlinebanking.dateTime.TransactionTimestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@Data
@NoArgsConstructor
public class DashboardService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;



    private BigDecimal totalMonthlyIncoming;
    private BigDecimal totalMonthlyOutcoming;
    private int accountsNumber;
    private int transactionsNumber;
    private int cardsNumber;
    private BigDecimal totalBalance;


    public int getAuthenticatedUserId()
    {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedUser.getUserId();
    }

    public boolean authenticatedUserAccountIsParticipantOfTransaction(Transaction transaction)
    {
        User authenticatedUser = userRepository.findByUserId(getAuthenticatedUserId());

        boolean transferringAccountBelongsToUser = authenticatedUser.getAccounts().contains(accountRepository.findByAccountId(transaction.getFromAccountId()));
        boolean receivingAccountBelongsToUser =  authenticatedUser.getAccounts().   contains(accountRepository.findByAccountId(transaction.getToAccountId()));

        return transferringAccountBelongsToUser || receivingAccountBelongsToUser;
    }


    public int getAuthenticatedUserAccountsNumber(){
        int authenticatedUserId = getAuthenticatedUserId();
        return userRepository.findByUserId(authenticatedUserId).getAccounts().size();
    }


    public int getAuthenticatedUserTransactionsNumber() {
        List<Transaction> authenticatedUserTransactionsList = transactionRepository.findAll().stream()
                .filter(this::authenticatedUserAccountIsParticipantOfTransaction)
                .toList();

        return authenticatedUserTransactionsList.size();
    }

    public int getAuthenticatedUserCardsNumber(){
        List<Card> authenticatedUserCardsList = cardRepository.findAll()
                .stream()
                .filter(card -> userRepository.findByUserId(getAuthenticatedUserId()).getAccounts().contains(card.getAccount()))
                .toList();

        return authenticatedUserCardsList.size();
    }

    public BigDecimal calculateTotalMonthlyIncoming() {
        int authenticatedUserId = getAuthenticatedUserId();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        List<Transaction> incomingTransactions = transactionRepository.findAll()
                .stream()
                .filter(transaction -> {
                    Account toAccount = accountRepository.findByAccountId(transaction.getToAccountId());
                    Account fromAccount = accountRepository.findByAccountId(transaction.getFromAccountId());
                    if (toAccount == null || fromAccount == null) {
                        return false; // Skip transactions with missing accounts
                    }
                    return toAccount.getUserId() == authenticatedUserId && ((fromAccount.getUserId() == authenticatedUserId && transaction.getTransactionType() == TransactionType.DEPOSIT) || (fromAccount.getUserId() != authenticatedUserId)) && new TransactionTimestamp().transactionOccurredWithinCurrentMonth(transaction, currentTimestamp);
                })
                .toList();

        return incomingTransactions.stream()
                .map(transaction -> {
                    BigDecimal amount = transaction.getAmount();
                    CurrencyType transactionCurrency = transaction.getCurrency();
                    Account toAccount = accountRepository.findByAccountId(transaction.getToAccountId());
                    if (toAccount == null) {
                        return BigDecimal.ZERO; // Skip transactions with missing accounts
                    }
                    CurrencyType receivingAccountCurrency = toAccount.getCurrency();

                    if (transactionCurrency != receivingAccountCurrency) {
                        if (receivingAccountCurrency != toAccount.getUser().getDefaultCurrency()) {
                            amount = amount.multiply(transaction.getToAccountDefaultCurrencyConversionRate());
                        }

                        BigDecimal amountConvertedToReceivingAccountCurrency = amount.multiply(transaction.getToAccountConversionRate());
                        return amountConvertedToReceivingAccountCurrency;
                    } else {
                        if (receivingAccountCurrency != toAccount.getUser().getDefaultCurrency()) {
                            amount = amount.multiply(transaction.getToAccountDefaultCurrencyConversionRate());
                        }

                        return amount;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalMonthlyOutcoming() {
        int authenticatedUserId = getAuthenticatedUserId();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        List<Transaction> outcomingTransactions = transactionRepository.findAll()
                .stream()
                .filter(transaction -> {
                    Account fromAccount = accountRepository.findByAccountId(transaction.getFromAccountId());
                    Account toAccount = accountRepository.findByAccountId(transaction.getToAccountId());
                    if (fromAccount == null || toAccount == null) {
                        return false; // Skip transactions with missing accounts
                    }
                    return fromAccount.getUserId() == authenticatedUserId && toAccount.getUserId() != authenticatedUserId && new TransactionTimestamp().transactionOccurredWithinCurrentMonth(transaction, currentTimestamp);
                })
                .toList();

        return outcomingTransactions.stream()
                .map(transaction -> {
                    BigDecimal amount = transaction.getAmount();
                    CurrencyType transactionCurrency = transaction.getCurrency();
                    Account fromAccount = accountRepository.findByAccountId(transaction.getFromAccountId());
                    if (fromAccount == null) {
                        return BigDecimal.ZERO; // Skip transactions with missing accounts
                    }
                    CurrencyType transferringAccountCurrency = fromAccount.getCurrency();

                    if (transactionCurrency != transferringAccountCurrency) {
                        if (transferringAccountCurrency != fromAccount.getUser().getDefaultCurrency()) {
                            amount = amount.multiply(transaction.getFromAccountDefaultCurrencyConversionRate());
                        }

                        BigDecimal amountConvertedToTransferringAccountCurrency = amount.multiply(transaction.getFromAccountConversionRate());
                        return amountConvertedToTransferringAccountCurrency;
                    } else {
                        if (transferringAccountCurrency != fromAccount.getUser().getDefaultCurrency()) {
                            amount = amount.multiply(transaction.getFromAccountDefaultCurrencyConversionRate());
                        }

                        return amount;
                    }

                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal calculateTotalBalance()
    {
        List<Account> allAccountsOfAuthenticatedUser = userRepository.findByUserId(getAuthenticatedUserId()).getAccounts();

        return allAccountsOfAuthenticatedUser.stream()
                .map(account -> {
                    CurrencyType accountCurrency = account.getCurrency();
                    CurrencyType authenticatedUserDefaultCurrency = userRepository.findByUserId(getAuthenticatedUserId()).getDefaultCurrency();
                    BigDecimal accountBalance = account.getBalance();

                    if (accountCurrency != authenticatedUserDefaultCurrency) {
                        BigDecimal defaultCurrencyConversionRate = new APIRequest().getExchangeRate(accountCurrency.toString(), authenticatedUserDefaultCurrency.toString());
                        return accountBalance.multiply(defaultCurrencyConversionRate);
                    }

                    else
                        return accountBalance;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }


    public DashboardDTO setDashboardFieldValues()
    {
           DashboardDTO dashboardDTO = new DashboardDTO();

           dashboardDTO.setAccountsNumber(getAuthenticatedUserAccountsNumber());
           dashboardDTO.setTransactionsNumber(getAuthenticatedUserTransactionsNumber());
           dashboardDTO.setCardsNumber(getAuthenticatedUserCardsNumber());
           dashboardDTO.setTotalBalance(calculateTotalBalance());
           dashboardDTO.setTotalIncoming(calculateTotalMonthlyIncoming());
           dashboardDTO.setTotalOutcoming(calculateTotalMonthlyOutcoming());

           return dashboardDTO;
    }

    public AccountDTO setAccountFieldValues(Account existingAccount)
    {
        AccountDTO accountDTO = new AccountDTO(existingAccount);
        return accountDTO;
    }
}