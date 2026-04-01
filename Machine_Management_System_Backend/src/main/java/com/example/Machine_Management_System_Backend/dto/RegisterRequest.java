package com.example.Machine_Management_System_Backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role;
    private String concernName;
}
