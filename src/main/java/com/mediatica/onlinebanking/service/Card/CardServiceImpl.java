package com.mediatica.onlinebanking.service.Card;

import com.mediatica.onlinebanking.models.Card;
import com.mediatica.onlinebanking.models.Transaction;

import java.util.List;

public interface CardServiceImpl {
    Card createCard(Card card);
    Card updateCard(int id, Card card);
    Card deleteCard(int id, Card card);
    Card getCardDetails(int id);
    List<Card> getAllCards();
    List<Transaction> getAllTransactions(int id, Card card);
}