package com.mediatica.onlinebanking.models;

import jakarta.persistence.*;
import com.mediatica.onlinebanking.enums.TransactionType;
import com.mediatica.onlinebanking.enums.CurrencyType;
import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @Column(name = "from_account_id")
    private int fromAccountId;

    @Column(name = "to_account_id")
    private int toAccountId;

    private BigDecimal amount;

    private CurrencyType currency;

    @Column(name = "from_account_conversion_rate", nullable = true)
    private BigDecimal fromAccountConversionRate;

    @Column(name = "to_account_conversion_rate", nullable = true)
    private BigDecimal toAccountConversionRate;

    @Column(name = "from_account_user_default_currency_conversion_rate", nullable = true)
    private BigDecimal fromAccountDefaultCurrencyConversionRate;

    @Column(name = "to_account_user_default_currency_conversion_rate", nullable = true)
    private BigDecimal toAccountDefaultCurrencyConversionRate;

    @Column(name = "transaction_type")
    private TransactionType transactionType;

    private String status;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Transaction(){
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(int fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(int toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CurrencyType getCurrency() { return this.currency; }

    public void setCurrency(CurrencyType currency) { this.currency = currency;}
    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getFromAccountConversionRate() {
        return fromAccountConversionRate;
    }

    public void setFromAccountConversionRate(BigDecimal fromAccountConversionRate) {
        this.fromAccountConversionRate = fromAccountConversionRate;
    }

    public BigDecimal getToAccountConversionRate() {
        return toAccountConversionRate;
    }

    public void setToAccountConversionRate(BigDecimal toAccountConversionRate) {
        this.toAccountConversionRate = toAccountConversionRate;
    }

    public BigDecimal getFromAccountDefaultCurrencyConversionRate() {
        return fromAccountDefaultCurrencyConversionRate;
    }

    public void setFromAccountDefaultCurrencyConversionRate(BigDecimal fromAccountDefaultCurrencyConversionRate) {
        this.fromAccountDefaultCurrencyConversionRate = fromAccountDefaultCurrencyConversionRate;
    }

    public BigDecimal getToAccountDefaultCurrencyConversionRate() {
        return toAccountDefaultCurrencyConversionRate;
    }

    public void setToAccountDefaultCurrencyConversionRate(BigDecimal toAccountDefaultCurrencyConversionRate)
    {
        this.toAccountDefaultCurrencyConversionRate = toAccountDefaultCurrencyConversionRate;
    }
}
