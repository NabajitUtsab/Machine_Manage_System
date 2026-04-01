package com.example.Machine_Management_System_Backend.controller;

import com.example.Machine_Management_System_Backend.entity.Machines;
import com.example.Machine_Management_System_Backend.entity.Transfer;
import com.example.Machine_Management_System_Backend.entity.User;
import com.example.Machine_Management_System_Backend.enumerations.MachineStatus;
import com.example.Machine_Management_System_Backend.repositories.TransferRepository;
import com.example.Machine_Management_System_Backend.repositories.UserRepository;
import com.example.Machine_Management_System_Backend.service.MachineService;
import com.example.Machine_Management_System_Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MachineService machineService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TransferRepository transferRepository;

    // ==================== MACHINES ====================

    @GetMapping("/machines")
    public List<Machines> getMachines(Authentication auth) {
        User admin = getAdminUser(auth);
        return machineService.getMachinesByConcern(admin.getConcern().getId());
    }


    @GetMapping("/machines/owned")
    public List<Machines> getOwnedMachines(Authentication auth) {
        User admin = getAdminUser(auth);
        return machineService.getAllMachines().stream()
                .filter(m -> m.getOriginConcern() != null &&
                        m.getOriginConcern().getId().equals(admin.getConcern().getId()))
                .collect(Collectors.toList());
    }


    @GetMapping("/machines/received")
    public List<Transfer> getReceivedMachines(Authentication auth) {
        User admin = getAdminUser(auth);
        return transferRepository.findByToConcern_Id(admin.getConcern().getId())
                .stream()
                .filter(t -> t.getStatus().name().equals("COMPLETED") || t.getStatus().name().equals("RETURNED"))
                .filter(t -> t.getFromConcern() != null &&
                        !t.getFromConcern().getId().equals(admin.getConcern().getId()))
                .collect(Collectors.toList());
    }


    @GetMapping("/machines/transferred-out")
    public List<Transfer> getTransferredOutMachines(Authentication auth) {
        User admin = getAdminUser(auth);
        return transferRepository.findByFromConcern_Id(admin.getConcern().getId());
    }


    @GetMapping("/machines/inhouse")
    public List<Machines> getInhouseMachines(Authentication auth) {
        User admin = getAdminUser(auth);
        return machineService.getMachinesByConcern(admin.getConcern().getId())
                .stream()
                .filter(m -> m.getStatus() != MachineStatus.TRANSFERRED)
                .collect(Collectors.toList());
    }


    @GetMapping("/notifications")
    public java.util.Map<String, Object> getNotifications(Authentication auth) {
        User admin = getAdminUser(auth);
        Long concernId = admin.getConcern().getId();

        List<Machines> readyMachines = machineService.getMachinesByConcern(concernId)
                .stream()
                .filter(m -> m.getStatus() == MachineStatus.READY_TO_TRANSFER)
                .collect(Collectors.toList());

        List<Transfer> incomingTransfers = transferRepository.findByToConcern_Id(concernId)
                .stream()
                .filter(t -> t.getStatus().name().equals("INITIATED"))
                .collect(Collectors.toList());

        List<Transfer> outgoingTransfers = transferRepository.findByFromConcern_Id(concernId)
                .stream()
                .filter(t -> t.getStatus().name().equals("INITIATED"))
                .collect(Collectors.toList());

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("readyToTransfer", readyMachines);
        result.put("incomingTransfers", incomingTransfers);
        result.put("outgoingTransfers", outgoingTransfers);
        return result;
    }

    @PutMapping("/machines/{id}")
    public Machines updateMachine(Authentication auth,
                                  @PathVariable Long id,
                                  @RequestParam(required = false) String groupName,
                                  @RequestParam(required = false) MachineStatus status) {
        User admin = getAdminUser(auth);
        Machines machine = machineService.getMachineById(id);
        if (!machine.getCurrentConcern().getId().equals(admin.getConcern().getId())) {
            throw new RuntimeException("Access denied: Machine not in your concern");
        }
        return machineService.updateMachine(id, groupName, status, null);
    }

    @DeleteMapping("/machines/{id}")
    public String deleteMachine(Authentication auth, @PathVariable Long id) {
        User admin = getAdminUser(auth);
        Machines machine = machineService.getMachineById(id);
        if (!machine.getCurrentConcern().getId().equals(admin.getConcern().getId())) {
            throw new RuntimeException("Access denied");
        }
        machineService.deleteMachine(id);
        return "Machine deleted";
    }

    // ==================== USERS ====================

    @GetMapping("/users")
    public List<User> getUsers(Authentication auth) {
        User admin = getAdminUser(auth);
        return userRepository.findByConcernId(admin.getConcern().getId());
    }

    // ==================== HELPERS ====================

    private User getAdminUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
    }
}