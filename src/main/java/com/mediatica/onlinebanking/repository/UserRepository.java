package com.mediatica.onlinebanking.repository;

import com.mediatica.onlinebanking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUserId(int userId);
    List<User> findAll();
}