package com.example.Machine_Management_System_Backend.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Long machineId;
    private String toConcernName;
}
