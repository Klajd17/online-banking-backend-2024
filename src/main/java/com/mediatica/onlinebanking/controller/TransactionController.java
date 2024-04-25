package com.mediatica.onlinebanking.controller;

import com.mediatica.onlinebanking.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mediatica.onlinebanking.service.Transaction.TransactionService;
import com.mediatica.onlinebanking.exceptions.BalanceExceededException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            String message = "Transaction with ID: " + createdTransaction.getTransactionId() + " processed successfully!";
            Map<String, String> response = Collections.singletonMap("message", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getAllTransactions(@PathVariable int id) {
        Transaction existingTransaction = transactionService.getTransactionDetails(id);

        if(existingTransaction != null)
            return ResponseEntity.ok(existingTransaction);

        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction with ID " + id + " not found!");
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
