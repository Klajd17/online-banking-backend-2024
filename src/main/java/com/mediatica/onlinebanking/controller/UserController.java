package com.mediatica.onlinebanking.controller;

import com.mediatica.onlinebanking.dto.UserDTO;
import com.mediatica.onlinebanking.enums.UserRole;
import com.mediatica.onlinebanking.models.User;
import com.mediatica.onlinebanking.security.AuthRequest;
import com.mediatica.onlinebanking.security.AuthResponse;
import com.mediatica.onlinebanking.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;

import com.mediatica.onlinebanking.service.User.UserService;
import com.mediatica.onlinebanking.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {

        try {

            List<UserDTO> users = userService.getAllUsers();

            if(users.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found.");

            else {

                int authenticatedUserId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
                UserRole authenticatedUserRole = userService.getUserById(authenticatedUserId).getRole();


                if (authenticatedUserRole == UserRole.ADMIN)
                    return ResponseEntity.ok(users);


                else if(authenticatedUserRole == UserRole.USER){
                    List<UserDTO> filteredUserDTOsList = users.stream()
                            .filter(user -> user.getUserId() == authenticatedUserId)
                            .toList();

                    return ResponseEntity.ok(filteredUserDTOsList);
                }

                else
                    return ResponseEntity.badRequest().body("User role is not being correctly processed!!!");
            }
        }


        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching users: " + e.getMessage());
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        // Validate user input
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return ResponseEntity.badRequest().body("Username, password, and email are required.");
        }

        try {
            User createdUser = userService.createUser(user);
            String message = "User created successfully with username: " + createdUser.getUsername();
            return ResponseEntity.ok(message);
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        try {
            UserDTO user = userService.getUserById(userId);

            if (user != null)
                return ResponseEntity.ok(user);
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        }

        catch (EntityNotFoundException e) {

            // Handle the case where the user is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            String updatedUser = userService.updateUser(user);

            if (updatedUser != null) {
                return ResponseEntity.ok("User Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }

        catch(Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId) {
        String message = userService.deleteUser(userId);

        if ("User Deleted Successfully".equals(message)) {
            return ResponseEntity.ok(message);
        } else if ("User not found".equals(message)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable int userId, @RequestBody String newPassword) {
        try {
            User user = userService.changePassword(userId, newPassword);
            return ResponseEntity.ok("Password changed successfully for user with ID: " + user.getUserId());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            UserDTO user = userService.getUserByUsername(userDetails.getUsername());
            AuthResponse response = new AuthResponse(userDetails.getUsername(), accessToken, userDetails.getEmail(),
                    user.getFullName(), user.getAddress(), user.getPhoneNumber(), user.getUserId());

            return ResponseEntity.ok().body(response);

        }

        catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok().body("You've been signed out!");
    }

    @GetMapping("/get-by-username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
            UserDTO userDTO = userService.getUserByUsername(username);
            if (userDTO != null) {
                return ResponseEntity.ok(userDTO);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username " + username + " not found!");
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