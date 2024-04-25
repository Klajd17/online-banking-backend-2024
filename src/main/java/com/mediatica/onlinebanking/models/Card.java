package com.mediatica.onlinebanking.models;

import com.mediatica.onlinebanking.enums.CardType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import jakarta.validation.constraints.Digits;

@Entity
@Table(name = "cards")
public class Card
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="card_id")
    private int cardId;

    @Column(name = "account_id")
    private int accountId;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private Account account;

    @Column(name = "pin", nullable = false)
    @Digits(integer = 4, fraction = 0, message = "Value must be a 4-digit number")
    private int pin;

    @Column(name = "card_number", unique = true, nullable = false, length=16)
    private String cardNumber;
    
    @Column(name = "expiry_date", nullable = false)
    private Timestamp expiryDate;

    @Column(name = "cvv", nullable = false)
    @Digits(integer = 3, fraction = 0, message = "Value must be a 3-digit number")
    private int cvv;

    @Column(name = "card_type", nullable = false, length = 50)
    private CardType cardType;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    
    
    public Card() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.cardNumber = Long.valueOf((long)(Math.random() * 10000000000000000L)).toString();
    }


    public int getCardId()
    {
        return cardId;
    }

    public void setCardId(int cardId)
    {
        this.cardId = cardId;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }


    public Timestamp getExpiryDate() 
    {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) 
    {
        this.expiryDate = expiryDate;
    }

    public int getCvv() 
    {
        return cvv;
    }

    public void setCvv(int cvv)
    {
        this.cvv = cvv;
    }

    public CardType getCardType()
    {
        return cardType;
    }

    public void setCardType(CardType cardType)
    {
        this.cardType = cardType;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }


    public Timestamp getCreatedAt() 
    {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) 
    {
        this.createdAt = createdAt;
    }


    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account) 
    {
        this.account = account;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
