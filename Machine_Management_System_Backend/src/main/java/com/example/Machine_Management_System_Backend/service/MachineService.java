package com.example.Machine_Management_System_Backend.service;

import com.example.Machine_Management_System_Backend.entity.Concern;
import com.example.Machine_Management_System_Backend.entity.Machines;
import com.example.Machine_Management_System_Backend.enumerations.MachineStatus;
import com.example.Machine_Management_System_Backend.repositories.ConcernRepository;
import com.example.Machine_Management_System_Backend.repositories.MachineRepository;
import com.example.Machine_Management_System_Backend.utility.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;
    private final ConcernRepository concernRepository;


    public Machines createMachine(String code, String groupName, Long concernId, MachineStatus status) throws Exception {

        // Check if machine code already exists
        if (machineRepository.findByCode(code).isPresent()) {
            throw new RuntimeException("Machine with code " + code + " already exists");
        }

        // Get concern
        Concern concern = concernRepository.findById(concernId)
                .orElseThrow(() -> new RuntimeException("Concern not found with ID: " + concernId));

        // Create machine
        Machines machine = new Machines();
        machine.setCode(code);
        machine.setGroupName(groupName);


        machine.setStatus(status != null ? status : MachineStatus.IDLE);

        machine.setOriginConcern(concern);
        machine.setCurrentConcern(concern);

        // Save machine first to get ID
        machine = machineRepository.save(machine);


        String qrText = String.valueOf(machine.getId());
        String qrPath = "./qrcodes/" + machine.getId() + ".png";


        File qrDir = new File("./qrcodes");
        if (!qrDir.exists()) {
            boolean created = qrDir.mkdirs();
            System.out.println("QR codes directory created: " + created);
        }

        // Generate QR code
        QRCodeGenerator.generateQRCode(qrText, qrPath, 300, 300);


        machine.setQrCodePath(qrPath);


        return machineRepository.save(machine);
    }

    public Machines getMachineById(Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found with ID: " + id));
    }

    public List<Machines> getAllMachines() {
        return machineRepository.findAll();
    }

    public List<Machines> getMachinesByConcern(Long concernId) {
        return machineRepository.findByCurrentConcernId(concernId);
    }

    
    public Machines updateMachine(Long id, String groupName, MachineStatus status, Long concernId) {
        Machines machine = getMachineById(id);

        if (groupName != null && !groupName.isEmpty()) {
            machine.setGroupName(groupName);
        }

        if (status != null) {
            machine.setStatus(status);
        }

        if (concernId != null) {
            Concern concern = concernRepository.findById(concernId)
                    .orElseThrow(() -> new RuntimeException("Concern not found"));
            machine.setCurrentConcern(concern);
        }

        return machineRepository.save(machine);
    }

    public Machines markReadyToTransfer(Long id) {
        Machines machine = getMachineById(id);
        machine.setStatus(MachineStatus.READY_TO_TRANSFER);
        return machineRepository.save(machine);
    }

    public void deleteMachine(Long id) {
        Machines machine = getMachineById(id);

        // Delete QR code file if exists
        if (machine.getQrCodePath() != null) {
            File qrFile = new File(machine.getQrCodePath());
            if (qrFile.exists()) {
                boolean deleted = qrFile.delete();
                System.out.println("QR code file deleted: " + deleted);
            }
        }

        machineRepository.deleteById(id);
    }
}