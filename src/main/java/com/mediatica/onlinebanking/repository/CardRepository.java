package com.mediatica.onlinebanking.repository;

import com.mediatica.onlinebanking.models.Card;
import com.mediatica.onlinebanking.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Integer> {
    Card findByCardId(int accountId);
    List<Card> findAll();

}