package com.triviumbuys.controller;

import com.triviumbuys.dto.*;
import com.triviumbuys.entity.Role;
import com.triviumbuys.entity.User;
import com.triviumbuys.entity.DeliveryPerson;
import com.triviumbuys.services.UserService;
import com.triviumbuys.services.DeliveryPersonService;
import com.triviumbuys.utils.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.getName());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setAddress(signupRequest.getAddress());
        user.setRole(Role.CUSTOMER); // Default Customer Role
        return userService.save(user);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {
        try {
            // 1. Try authenticating as User (Admin / Customer)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Load UserDetails for JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String jwt = jwtUtil.generateToken(userDetails);

            // Load User entity
            User user = userService.findByUsername(request.getUsername());

            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(jwt);
            response.setRole(user.getRole().name());
            response.setUsername(user.getUsername()); // ✅ Add username
            if (user.getRole() == Role.ADMIN) {
                response.setAccessRights(user.getAccessRights()); // ✅ Only if ADMIN
            } else {
                response.setAccessRights(new HashSet<>()); // ✅ Empty if not admin
            }
            return response;

        } catch (Exception ex) {
            // 2. If normal User authentication fails, try DeliveryPerson authentication manually
            DeliveryPerson deliveryPerson = deliveryPersonService.getByUsername(request.getUsername());

            if (deliveryPerson == null) {
                throw new RuntimeException("Invalid Credentials");
            }

            // Check password manually (since DeliveryPerson is stored separately)
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(request.getPassword(), deliveryPerson.getPassword())) {
                throw new RuntimeException("Invalid Credentials");
            }

            // Generate JWT manually
            String jwt = jwtUtil.generateTokenForDeliveryPerson(deliveryPerson.getUsername());

            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(jwt);
            response.setRole("DELIVERY");
            response.setUsername(deliveryPerson.getUsername()); // ✅ Username for delivery too
            response.setAccessRights(new HashSet<>()); // ✅ Delivery has no access rights
            return response;
        }
    }
}
