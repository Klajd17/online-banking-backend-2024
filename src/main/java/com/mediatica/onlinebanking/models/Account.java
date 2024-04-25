package com.mediatica.onlinebanking.models;

import com.mediatica.onlinebanking.enums.CurrencyType;
import jakarta.persistence.*;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="account_id")
    private int accountId;

    @Column(name = "user_id")
    private int userId;
    @Column(name = "account_name", unique = true, nullable = false, length = 20)
    private String accountName;

    @Column(name = "account_number", unique = true, nullable = false, length = 28)
    private String accountNumber;

    @Column(precision = 15, scale = 2, columnDefinition = "DECIMAL(15, 2) DEFAULT 0.00")
    private BigDecimal balance;

    private CurrencyType currency;

    @Column(name = "account_type", length = 50)
    private String accountType;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;


    public Account() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.accountNumber = Iban.random(CountryCode.AL).toString();
    }
    
    public int getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public CurrencyType getCurrency() { return this.currency; }

    public void setCurrency(CurrencyType currency) { this.currency = currency;}
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}