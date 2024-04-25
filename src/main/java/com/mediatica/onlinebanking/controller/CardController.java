package com.mediatica.onlinebanking.controller;

import com.mediatica.onlinebanking.models.Card;
import com.mediatica.onlinebanking.models.Transaction;
import com.mediatica.onlinebanking.service.Card.CardService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {
    
    @Autowired
    private CardService cardService;

    @GetMapping("/all")
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }
    

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getCardDetails(@PathVariable int id) {
        
        Card existingCard = cardService.getCardDetails(id);
        
        if(existingCard != null)
            return ResponseEntity.ok(existingCard);
        
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card with ID " + id + " not found!");
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> getAllTransactions(@PathVariable int id, @RequestBody Card card)
    {
        List<Transaction> transactionsList = cardService.getAllTransactions(id, card);

        if(transactionsList != null)
            return ResponseEntity.ok(transactionsList);

        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions found for card with id: " + "!");
    }

    @PostMapping("/add")
    public ResponseEntity<String> createAccount(@Valid @RequestBody Card card)
    {

        //There is an exception expected when any field value is invalid field
        // For example, when the 'cvv' field gets an 'int' value with more than 3 digits, the '@Valid' annotation helps with the fields validation process and raises the exception.
        try
        {
            Card createdCard = cardService.createCard(card);

            if (createdCard != null)
                return ResponseEntity.ok("New card successfully created, linked with the account with number: " + createdCard.getAccountId());
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create the card!");
        }

        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation failed: " + e.getMessage());
        }
    }



    
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAccount(@Valid @PathVariable int id, @RequestBody Card card)
    {

        try
        {
            Card cardUpdated = cardService.updateCard(id, card);

            if (cardUpdated != null)
                return ResponseEntity.ok("Card with ID " + id + " updated.");
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card with ID: " + id + " not found!");
        }

        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Validation failed: " + e.getMessage());
        }


    }
    
 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable int id, @RequestBody Card card)
    {
        Card accountDeleted =  cardService.deleteCard(id, card);

        if(accountDeleted != null)
            return ResponseEntity.ok("Account with ID " + id + " successfully deleted!");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with ID " + id + " not found!");

    }

    @Configuration
    @EnableWebMvc
    public class CorsConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowCredentials(true);
        }
    }
}