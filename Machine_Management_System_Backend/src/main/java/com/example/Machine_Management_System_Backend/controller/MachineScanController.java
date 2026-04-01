package com.example.Machine_Management_System_Backend.controller;

import com.example.Machine_Management_System_Backend.entity.Machines;
import com.example.Machine_Management_System_Backend.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/machines")
@RequiredArgsConstructor
public class MachineScanController {

    private final MachineService machineService;

    @GetMapping("/{id}")
    public Machines getMachine(@PathVariable Long id) {
        return machineService.getMachineById(id);
    }
}