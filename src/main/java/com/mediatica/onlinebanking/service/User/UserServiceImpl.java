package com.mediatica.onlinebanking.service.User;

import com.mediatica.onlinebanking.models.User;

import java.util.List;

public interface UserServiceImpl {
    User createUser(User user);

    List<User> getAllUsers();

    public String updateUser(User user);
    public String deleteUser(String userId);

    User getUserById(int userId);

    User changePassword(int userId, String newPassword);

    String hashPassword(String plainTextPassword);

    User login(String username, String plainTextPassword);
}