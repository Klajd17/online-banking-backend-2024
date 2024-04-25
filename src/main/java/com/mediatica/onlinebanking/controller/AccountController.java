package com.mediatica.onlinebanking.controller;

import com.mediatica.onlinebanking.dto.AccountDTO;
import com.mediatica.onlinebanking.enums.UserRole;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.models.User;
import com.mediatica.onlinebanking.repository.UserRepository;
import com.mediatica.onlinebanking.service.Account.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    public List<AccountDTO> getAllAccounts()
    {
        int authenticatedUserId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        if(userRepository.findByUserId(authenticatedUserId).getRole() == UserRole.ADMIN)
            return accountService.getAllAccounts();

        else {
            return accountService.getAllAccounts()
                    .stream()
                    .filter(accountDTO -> accountDTO.getUserId() == authenticatedUserId)
                    .toList();
        }
    }
    
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getAccountDetails(@PathVariable int id) {
        
        AccountDTO existingAccountDTO = accountService.getAccountDetails(id);
        
        if(existingAccountDTO != null)
            return ResponseEntity.ok(existingAccountDTO);

        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with ID " + id + " not found!");
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> createAccount(@Valid @RequestBody Account account) {
        try {
            Account createdAccount = accountService.createAccount(account);

            if (createdAccount != null) {
                String message = "New account successfully created for user with id: " + createdAccount.getUserId() + "!";
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", message);
                return ResponseEntity.ok(response);
            } else {
                String errorMessage = "Failed to create the account for user with id: " + createdAccount.getUserId() + "!";
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", errorMessage);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAccount(@Valid @PathVariable int id, @RequestBody Account account)
    {

        try
        {
            Account accountUpdated = accountService.updateAccount(id, account);

            if (accountUpdated != null)
                return ResponseEntity.ok("Account with ID " + id + " updated.");
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with ID: " + id + " not found!");
        }

        catch(Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable int id, @RequestBody Account account) 
    {
        try
        {
            Account accountDeleted = accountService.deleteAccount(id);

            if (accountDeleted != null)
                return ResponseEntity.ok("Account with ID " + id + " successfully deleted!");
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with ID " + id + " not found!");
        }

        catch(Exception e)
        {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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