package com.mediatica.onlinebanking.controller;

import com.mediatica.onlinebanking.dto.AccountDTO;
import com.mediatica.onlinebanking.dto.DashboardDTO;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.models.Transaction;

import com.mediatica.onlinebanking.repository.AccountRepository;
import com.mediatica.onlinebanking.repository.TransactionRepository;
import com.mediatica.onlinebanking.service.Dashboard.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AccountRepository accountRepository;


    @GetMapping("/")
    public ResponseEntity<?> getAuthenticatedUserInfo()
    {

        // The dashboard information will be provided as an instance of 'DashboardDTO' class.
        // Every info will be provided as a respective field value.
        try {
            // Field values are set in the instance's setter methods, called inside the 'setFieldValues()' method definition.
            DashboardDTO dashboardDTO = dashboardService.setDashboardFieldValues();

            // Finally, the dashboard info will be returned as an object.
            // Each field of this object contains the necessary information.
            return ResponseEntity.ok(dashboardDTO);
        }


        catch(Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //This endpoint will serve as an information provider for any account, with the specified 'id' in the path.
    @GetMapping("/accounts/{id}")

    public ResponseEntity<?> getAccountDetails(@PathVariable int id) {

        try {

        Account existingAccount = accountRepository.findByAccountId(id);

        AccountDTO accountDTO = dashboardService.setAccountFieldValues(existingAccount);

        return ResponseEntity.ok(accountDTO);
    }

    catch(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    }
}
