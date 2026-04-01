package com.example.Machine_Management_System_Backend.controller;

import com.example.Machine_Management_System_Backend.entity.Machines;
import com.example.Machine_Management_System_Backend.entity.Transfer;
import com.example.Machine_Management_System_Backend.entity.User;
import com.example.Machine_Management_System_Backend.repositories.UserRepository;
import com.example.Machine_Management_System_Backend.service.ConcernService;
import com.example.Machine_Management_System_Backend.service.MachineService;
import com.example.Machine_Management_System_Backend.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final MachineService machineService;
    private final UserRepository userRepository;
    private final TransferService transferService;
    private final ConcernService concernService;

    // Mark machine as ready to transfer
    @PutMapping("/machines/{id}/ready")
    public Machines markReady(Authentication auth, @PathVariable Long id) {
        User user = getUser(auth);
        Machines machine = machineService.getMachineById(id);
        if (!machine.getCurrentConcern().getId().equals(user.getConcern().getId())) {
            throw new RuntimeException("You cannot update machine outside your concern");
        }
        return machineService.markReadyToTransfer(id);
    }

    // User initiates a transfer (marks ready then initiates)
    @PostMapping("/machines/{id}/transfer")
    public Transfer initiateTransfer(Authentication auth,
                                     @PathVariable Long id,
                                     @RequestParam String toConcernName) {
        User user = getUser(auth);
        Machines machine = machineService.getMachineById(id);

        if (!machine.getCurrentConcern().getId().equals(user.getConcern().getId())) {
            throw new RuntimeException("You cannot transfer machine outside your concern");
        }

        // Auto mark ready if not already
        if (machine.getStatus().name().equals("IDLE") || machine.getStatus().name().equals("ACTIVE")) {
            machineService.markReadyToTransfer(id);
        }

        Long toConcernId = concernService.getConcernByName(toConcernName).getId();
        return transferService.initiateTransfer(id, toConcernId);
    }

    private User getUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}