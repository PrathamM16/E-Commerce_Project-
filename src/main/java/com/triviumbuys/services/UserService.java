package com.triviumbuys.services;

import com.triviumbuys.entity.User;
import com.triviumbuys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> save(User user) {
        Map<String, String> response = new HashMap<>();

        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            response.put("error", "Email already exists. Please use another email.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent()) {
            response.put("error", "Username already exists. Please choose another username.");
            return ResponseEntity.badRequest().body(response);
        }

        userRepository.save(user);
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
    
    public List<User> findAllCustomers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("CUSTOMER"))
                .collect(Collectors.toList());
    }

    
    public Optional<User> findCustomerById(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole().name().equals("CUSTOMER"));
    }

    
    public List<User> findCustomersByName(String name) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("CUSTOMER"))
                .filter(user -> user.getName() != null && user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    
}
