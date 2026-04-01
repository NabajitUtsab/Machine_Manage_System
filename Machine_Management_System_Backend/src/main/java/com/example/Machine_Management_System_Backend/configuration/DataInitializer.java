package com.example.Machine_Management_System_Backend.configuration;

import com.example.Machine_Management_System_Backend.entity.User;
import com.example.Machine_Management_System_Backend.enumerations.Role;
import com.example.Machine_Management_System_Backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Check if any SUPER_ADMIN exists
        boolean superAdminExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getRole() == Role.SUPER_ADMIN);

        if (!superAdminExists) {
            // Create default SUPER_ADMIN
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            superAdmin.setRole(Role.SUPER_ADMIN);
            superAdmin.setConcern(null);

            userRepository.save(superAdmin);

            System.out.println("==============================================");
            System.out.println("DEFAULT SUPER_ADMIN CREATED");
            System.out.println("Username: superadmin");
            System.out.println("Password: admin123");
            System.out.println("PLEASE CHANGE PASSWORD AFTER FIRST LOGIN!");
            System.out.println("==============================================");
        }
    }
}