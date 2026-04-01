package com.example.Machine_Management_System_Backend.controller;


import com.example.Machine_Management_System_Backend.dto.LoginRequest;
import com.example.Machine_Management_System_Backend.dto.RegisterRequest;
import com.example.Machine_Management_System_Backend.entity.Concern;
import com.example.Machine_Management_System_Backend.entity.User;
import com.example.Machine_Management_System_Backend.enumerations.Role;
import com.example.Machine_Management_System_Backend.repositories.ConcernRepository;
import com.example.Machine_Management_System_Backend.repositories.UserRepository;
import com.example.Machine_Management_System_Backend.service.UserService;
import com.example.Machine_Management_System_Backend.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ConcernRepository concernRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;


    private ResponseEntity<?> registerUser(RegisterRequest req) {

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        try {
            user.setRole(Role.valueOf(req.getRole()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role. Must be: SUPER_ADMIN, ADMIN, or USER");
        }

        if (req.getConcernName() != null && !req.getConcernName().isEmpty()) {
            Concern concern = concernRepository.findConcernByName(req.getConcernName())
                    .orElseThrow(() -> new RuntimeException("Concern not found with name: " + req.getConcernName()));
            user.setConcern(concern);
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());
        response.put("concernName", user.getConcern() != null ? user.getConcern().getName() : null);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        try {
            // Authentication of the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        UserDetails userDetails = userService.loadUserByUsername(req.getUsername());
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));



        String token = jwtUtil.generateToken(userDetails, "ROLE_" + user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", token);
        response.put("role", user.getRole().name());
        response.put("concernId", user.getConcern() != null ? user.getConcern().getId() : null);
        response.put("concernName", user.getConcern() != null ? user.getConcern().getName() : null);
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/superadmin/register")
    public ResponseEntity<?> registerBySuperAdmin(@RequestBody RegisterRequest req, Authentication auth) {


        User caller = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Super_Admin not found"));

        if (caller.getRole() != Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body("Only SUPER_ADMIN can use this endpoint");
        }

        return registerUser(req);
    }


    @PostMapping("/admin/register")
    public ResponseEntity<?> registerByAdmin(@RequestBody RegisterRequest req, Authentication auth) {


        User caller = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        if (caller.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Only ADMIN can use this endpoint");
        }


        if (!req.getRole().equals("USER")) {
            return ResponseEntity.badRequest().body("Admin can only create USER role");
        }


        if (caller.getConcern() != null) {
            req.setConcernName(caller.getConcern().getName());
        }

        return registerUser(req);
    }
}
