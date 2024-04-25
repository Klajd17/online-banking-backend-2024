package com.mediatica.onlinebanking.dto;

import com.mediatica.onlinebanking.enums.CurrencyType;
import com.mediatica.onlinebanking.enums.UserRole;
import com.mediatica.onlinebanking.models.Account;
import com.mediatica.onlinebanking.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO {

    private int userId;

    private List<AccountDTO> accounts;

    private String username;

    private String email;

    private String fullName;

    private String address;

    private String phoneNumber;

    private UserRole role;

    private CurrencyType defaultCurrency;

    public UserDTO(User user){

        setUserId(user.getUserId());
        setAddress(user.getAddress());
        setUsername(user.getUsername());

        // The accounts list will contain AccountDTO objects, instead of Account entity full records.
        List<AccountDTO> accountDTOList = user.getAccounts()
                        .stream()
                        .map(AccountDTO::new)
                        .toList();
        setAccounts(accountDTOList);

        setEmail(user.getEmail());
        setRole(user.getRole());
        setFullName(user.getFullName());
        setPhoneNumber(user.getPhoneNumber());
        setDefaultCurrency(user.getDefaultCurrency());
    }


}
