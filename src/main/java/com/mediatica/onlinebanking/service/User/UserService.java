package com.mediatica.onlinebanking.service.User;

import com.mediatica.onlinebanking.dto.UserDTO;
import com.mediatica.onlinebanking.enums.UserRole;
import com.mediatica.onlinebanking.models.User;
import com.mediatica.onlinebanking.repository.UserRepository;
import com.mediatica.onlinebanking.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public User createUser(User user) {
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        if(user.getRole() == null){
            user.setRole(UserRole.USER);
        }
        return userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {

        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .map(UserDTO::new)
                .toList();
    }

    public String updateUser(User user) {
        String hashedPassword = hashPassword(user.getPassword());

        user.setPassword(hashedPassword);
        user.setUpdatedAt(new Date());
        userRepository.save(user);
        return "Updated Successfully";
    }


    public String deleteUser(String userId) {
        int id = Integer.valueOf(userId);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "User Deleted Successfully";
        } else {
            return "User not found";
        }
    }


    public UserDTO getUserById(int userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        UserDTO userDTO = new UserDTO(existingUser);
        return userDTO;
    }


    public User changePassword(int userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Hash the new password
        String hashedPassword = hashPassword(newPassword);

        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }


    public String hashPassword(String plainTextPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(plainTextPassword);
    }



    public User login(String username, String plainTextPassword) {

        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new EntityNotFoundException("User not found with username: " + username);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(plainTextPassword, user.getPassword()))
            throw new AuthenticationException("Incorrect password") {};

        return user;
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new EntityNotFoundException("User with username: " + username + "not found ");
        }
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }

        return UserDetailsImpl.build(user);
    }

    private UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}