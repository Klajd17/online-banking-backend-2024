package com.mediatica.onlinebanking.service.Transaction;

import com.mediatica.onlinebanking.enums.CurrencyType;
import com.mediatica.onlinebanking.exceptions.CurrencyMismatchException;
import com.mediatica.onlinebanking.exceptions.TransferException;
import com.mediatica.onlinebanking.models.Transaction;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.enums.TransactionType;
import com.mediatica.onlinebanking.repository.AccountRepository;
import com.mediatica.onlinebanking.repository.TransactionRepository;
import com.mediatica.onlinebanking.exceptions.BalanceExceededException;
import com.mediatica.onlinebanking.currencyAPI.APIRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService implements TransactionServiceImpl {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Transaction createTransaction(Transaction transaction) {


        TransactionType transactionType = transaction.getTransactionType();
        int transferringAccountId = transaction.getFromAccountId();
        int receivingAccountId = transaction.getToAccountId();

        BigDecimal amount = transaction.getAmount();
        CurrencyType transactionCurrency = transaction.getCurrency();
        BigDecimal exchangeRate;


        if(transactionType == TransactionType.WITHDRAWAL)
        {
            if(transferringAccountId != receivingAccountId)
                throw new CurrencyMismatchException("Withdrawal transaction has only one participant account, so make sure the account IDs are identical!");

            if(transactionCurrency != accountRepository.findByAccountId(transferringAccountId).getCurrency())
                throw new CurrencyMismatchException("This transaction can not proceed, if its currency does not match the participating account's one!");

            Account transferringAccount = accountRepository.findByAccountId(transferringAccountId);
            BigDecimal fromAccountBalance = transferringAccount.getBalance();

            if(amount.compareTo(fromAccountBalance) < 0)
            {
                BigDecimal updatedBalance = fromAccountBalance.subtract(amount);
                transferringAccount.setBalance(updatedBalance);

                // Rows below will serve to the dashboard, specifically providing the transaction's amount as an outcoming one.
                // This way, it helps on its side for the calculation of the total amount of outcoming from an account.
                CurrencyType transferringAccountUserDefaultCurrency = transferringAccount.getUser().getDefaultCurrency(); // Currency of the user which the transferring account belongs to

                if(transaction.getCurrency() != transferringAccountUserDefaultCurrency) {
                    exchangeRate = new APIRequest().getExchangeRate(transactionCurrency.toString(), transferringAccountUserDefaultCurrency.toString());
                    transaction.setFromAccountDefaultCurrencyConversionRate(exchangeRate);
                }
            }

            else
                throw new BalanceExceededException("The amount provided exceeds the available balance! Please, make sure the amount is lower or equal to it!");
        }



        else if(transactionType == TransactionType.DEPOSIT)
        {
            if(transferringAccountId != receivingAccountId)
                throw new CurrencyMismatchException("Deposit transaction has only one participant account, so make sure the account IDs are identical!");

            if(transactionCurrency != accountRepository.findByAccountId(transferringAccountId).getCurrency())
                throw new CurrencyMismatchException("This transaction can not proceed, if its currency does not match the participating account's one!");

            Account transferringAccount = accountRepository.findByAccountId(transferringAccountId);
            BigDecimal updatedBalance = transferringAccount.getBalance().add(amount);
            transferringAccount.setBalance(updatedBalance);

            // Rows below will serve to the dashboard, specifically providing the transaction's amount as an incoming one.
            // This way, it helps on its side for the calculation of the total amount of incoming into an account.
            CurrencyType transferringAccountUserDefaultCurrency = transferringAccount.getUser().getDefaultCurrency(); // Currency of the user which the transferring account belongs to

            if(transaction.getCurrency() != transferringAccountUserDefaultCurrency) {
                exchangeRate = new APIRequest().getExchangeRate(transactionCurrency.toString(), transferringAccountUserDefaultCurrency.toString());
                transaction.setToAccountDefaultCurrencyConversionRate(exchangeRate);
            }
        }

        else if(transactionType == TransactionType.TRANSFER)
        {
            if(transferringAccountId == receivingAccountId)
                throw new TransferException("The account cannot make transfer to itself! Please, check again the transaction's participants accounts!");

            Account transferringAccount = accountRepository.findByAccountId(transferringAccountId);
            BigDecimal transferringAccountBalance = transferringAccount.getBalance();


            BigDecimal transferringAccountCurrencyAmount; //Amount of transaction, which matches the transferring account's currency

            if(transactionCurrency == transferringAccount.getCurrency())
                transferringAccountCurrencyAmount = amount;

            //Sometimes, a user has to make a transfer, whose currency is different from his/her account currency type.
            //This way, a conversion needs to take place in this situation for 2 obvious reasons.
            //First, in order for the transfer to be completed, the amount of the transaction must not be greater than the balance.
            //So, in order to check this, we need the amount of the transfer to be in the account's currency.
            //Second, the account balance needs to be updated and the transfer amount must be subtracted from the balance in the account currency's type.
            else
            {
                exchangeRate = new APIRequest().getExchangeRate(transactionCurrency.toString(), transferringAccount.getCurrency().toString());
                transferringAccountCurrencyAmount = amount.multiply(exchangeRate);
                transaction.setFromAccountConversionRate(exchangeRate);
            }

            if(transferringAccountCurrencyAmount.compareTo(transferringAccountBalance) < 0)
            {
                Account receivingAccount = accountRepository.findByAccountId(receivingAccountId);

                CurrencyType transferringAccountCurrency = transferringAccount.getCurrency();
                CurrencyType receivingAccountCurrency = receivingAccount.getCurrency();


                BigDecimal transferringAccountUpdatedBalance = transferringAccount.getBalance().subtract(transferringAccountCurrencyAmount);

                //The receiving account has its currency type.
                //There are cases when the currency type specified in the transaction is different from it.
                //So, a currency conversion has to take place in this situation.
                //The amount of the currency specified in the transaction has to be converted into the receiver's currency.
                //So, we can update the 'amount' variable that stores the transaction specified amount and multiply it with the currency range.
                if(!receivingAccountCurrency.toString().equals(transactionCurrency.toString()))
                {
                    exchangeRate = new APIRequest().getExchangeRate(transactionCurrency.toString(), receivingAccountCurrency.toString());
                    amount = amount.multiply(exchangeRate);
                    transaction.setToAccountConversionRate(exchangeRate);
                }


                BigDecimal receivingAccountUpdatedBalance = receivingAccount.getBalance().add(amount);

                //Finally, the balance update is done for both accounts, using the 'setters' below.
                transferringAccount.setBalance(transferringAccountUpdatedBalance);
                receivingAccount.setBalance(receivingAccountUpdatedBalance);

                // We should also compare the participant accounts' currency with their user's default currency.
                // This is useful in the most complex situation when accounts' currency is different from their user's default currency set.
                CurrencyType receivingAccountUserDefaultCurrency = accountRepository.findByAccountId(receivingAccountId).getUser().getDefaultCurrency();
                if(receivingAccountCurrency != receivingAccountUserDefaultCurrency)
                {
                    exchangeRate = new APIRequest().getExchangeRate(receivingAccountCurrency.toString(), receivingAccountUserDefaultCurrency.toString());
                    transaction.setToAccountDefaultCurrencyConversionRate(exchangeRate);
                }

                CurrencyType transferringAccountUserDefaultCurrency = accountRepository.findByAccountId(transferringAccountId).getUser().getDefaultCurrency();
                if(transferringAccountCurrency != transferringAccountUserDefaultCurrency)
                {
                    exchangeRate = new APIRequest().getExchangeRate(transferringAccountCurrency.toString(), transferringAccountUserDefaultCurrency.toString());
                    transaction.setFromAccountDefaultCurrencyConversionRate(exchangeRate);
                }


            }

            else
                throw new BalanceExceededException("The amount of transfer provided exceeds the available balance! Please, make sure the amount is lower or equal to it!");
        }

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionDetails(int id)
    {
        Transaction existingTransaction = transactionRepository.findByTransactionId(id);

        if(existingTransaction != null)
            return existingTransaction;

        else
            return null;

    }
}