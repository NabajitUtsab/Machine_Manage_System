package com.example.Machine_Management_System_Backend.controller;

import com.example.Machine_Management_System_Backend.entity.*;
import com.example.Machine_Management_System_Backend.enumerations.MachineStatus;
import com.example.Machine_Management_System_Backend.enumerations.TransferStatus;
import com.example.Machine_Management_System_Backend.repositories.*;
import com.example.Machine_Management_System_Backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final UserRepository userRepository;
    private final ConcernService concernService;
    private final MachineService machineService;
    private final TransferService transferService;
    private final MachineRepository machineRepository;
    private final TransferRepository transferRepository;

    // ==================== USERS ====================

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted";
    }

    // ==================== CONCERNS ====================

    @PostMapping("/concerns")
    public Concern addConcern(@RequestParam String name) {
        return concernService.createConcern(name);
    }

    @PutMapping("/concerns/{id}")
    public Concern updateConcern(@PathVariable Long id, @RequestParam String name) {
        return concernService.updateConcern(id, name);
    }

    @DeleteMapping("/concerns/{id}")
    public String deleteConcern(@PathVariable Long id) {
        concernService.deleteConcern(id);
        return "Concern deleted";
    }

    @GetMapping("/concerns")
    public List<Concern> allConcerns() {
        return concernService.getAllConcerns();
    }

    // ==================== MACHINES ====================

    @PostMapping("/machines")
    public Machines addMachine(@RequestParam String code,
                               @RequestParam String groupName,
                               @RequestParam String concernName,
                               @RequestParam(required = false) String status) throws Exception {
        Concern concern = concernService.getConcernByName(concernName);
        MachineStatus machineStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                machineStatus = MachineStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                machineStatus = MachineStatus.IDLE;
            }
        }
        return machineService.createMachine(code, groupName, concern.getId(), machineStatus);
    }

    @PutMapping("/machines/{id}")
    public Machines updateMachine(@PathVariable Long id,
                                  @RequestParam(required = false) String groupName,
                                  @RequestParam(required = false) MachineStatus status,
                                  @RequestParam(required = false) String concernName) {
        Long concernId = null;
        if (concernName != null && !concernName.isEmpty()) {
            Concern concern = concernService.getConcernByName(concernName);
            concernId = concern.getId();
        }
        return machineService.updateMachine(id, groupName, status, concernId);
    }

    @DeleteMapping("/machines/{id}")
    public String deleteMachine(@PathVariable Long id) {
        machineService.deleteMachine(id);
        return "Machine deleted";
    }

    @GetMapping("/machines")
    public List<Machines> allMachines() {
        return machineService.getAllMachines();
    }

    // ==================== TRANSFERS ====================

    @GetMapping("/transfers")
    public List<Transfer> allTransfers() {
        return transferService.getAllTransfers();
    }

    @DeleteMapping("/transfers/{id}")
    public String deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return "Transfer deleted";
    }

    // ==================== NOTIFICATIONS ====================

    @GetMapping("/notifications")
    public Map<String, Object> getNotifications() {

        List<Machines> readyMachines = machineRepository.findAll()
                .stream()
                .filter(m -> m.getStatus() == MachineStatus.READY_TO_TRANSFER)
                .collect(Collectors.toList());


        List<Transfer> initiatedTransfers = transferRepository.findAll()
                .stream()
                .filter(t -> t.getStatus() == TransferStatus.INITIATED)
                .collect(Collectors.toList());


        List<Transfer> completedTransfers = transferRepository.findAll()
                .stream()
                .filter(t -> t.getStatus() == TransferStatus.COMPLETED)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("readyToTransfer", readyMachines);
        result.put("initiatedTransfers", initiatedTransfers);
        result.put("completedTransfers", completedTransfers);
        return result;
    }
}