package com.example.Machine_Management_System_Backend.dto;


import lombok.Data;

@Data
public class MachineRequest {
    private String code;
    private String groupName;
    private String status;
    private String concernName;
}