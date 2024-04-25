package com.mediatica.onlinebanking.dateTime;

import com.mediatica.onlinebanking.models.Transaction;

import java.sql.Timestamp;

public class TransactionTimestamp {
    public boolean transactionOccurredWithinCurrentMonth(Transaction transaction, Timestamp currentTimestamp)
    {
        boolean transactionOccurredLaterAfterFirstDayOfCurrentMonth = transaction.getCreatedAt().compareTo(FirstLastDayOfMonth.getFirstDayOfMonth(currentTimestamp)) >= 0;
        boolean transactionOccurredBeforeLastDayOfCurrentMonth = transaction.getCreatedAt().compareTo(FirstLastDayOfMonth.getLastDayOfMonth(currentTimestamp)) <= 0;

        return transactionOccurredLaterAfterFirstDayOfCurrentMonth && transactionOccurredBeforeLastDayOfCurrentMonth;

    }
}
