package com.example.Machine_Management_System_Backend.dto;


import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}