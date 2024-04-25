package com.mediatica.onlinebanking.service.Card;

import com.mediatica.onlinebanking.models.Card;
import com.mediatica.onlinebanking.models.Transaction;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.repository.CardRepository;
import com.mediatica.onlinebanking.generics.RecordUpdate;
import com.mediatica.onlinebanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
public class CardService implements CardServiceImpl {
    
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Card createCard(Card card) {
        return cardRepository.save(card);
    }
    
    @Override
    public List<Card> getAllCards() {
        // More Business Logic
        return cardRepository.findAll();
    }

    @Override
    public Card getCardDetails(int id)
    {
        Card existingCard = cardRepository.findByCardId(id);

        if(existingCard != null)
            return existingCard;

        else
            return null;

    }

    @Override
    public List<Transaction> getAllTransactions(int id, Card card)
    {
        Card existingCard = cardRepository.findByCardId(id);

        if(existingCard != null)
        {
            int relatedAccountId = existingCard.getAccount().getAccountId();

            List<Transaction> incomingTransactions = transactionRepository.findByFromAccountId(relatedAccountId);
            List<Transaction> outcomingTransactions = transactionRepository.findByToAccountId(relatedAccountId);

            //Here are both types of transactions concatenated into a new list.
            List<Transaction> allTransactions = new ArrayList<Transaction>();
            allTransactions.addAll(incomingTransactions);
            allTransactions.addAll(outcomingTransactions);

            return allTransactions;
        }

        else
            return null;

    }

    @Override
    public Card updateCard(int id, Card card) {

        Card existingCard = cardRepository.findByCardId(id);

        if(existingCard != null)
        {
            RecordUpdate<Card, Card, CardRepository> genericClassInstance = new RecordUpdate<Card, Card, CardRepository>(existingCard, card, cardRepository);
            genericClassInstance.updateRecord();

            // Save the updated 'existingCard'
            Card updatedCard = cardRepository.save(existingCard);
            if(updatedCard != null)
                return updatedCard;
            else
                return null;
        }

        else
            return null; // Handle the situation when the card is not found
    }


    @Override
    public Card deleteCard(int id, Card account) {
        
        Card existingCard = cardRepository.findByCardId(id);
        
        if (existingCard != null)
        {
            cardRepository.delete(existingCard);
            return existingCard;
        }

        else
            return null;
    }
}