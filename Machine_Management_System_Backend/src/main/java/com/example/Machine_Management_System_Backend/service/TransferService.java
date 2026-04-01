package com.example.Machine_Management_System_Backend.service;

import com.example.Machine_Management_System_Backend.entity.Concern;
import com.example.Machine_Management_System_Backend.entity.Machines;
import com.example.Machine_Management_System_Backend.entity.Transfer;
import com.example.Machine_Management_System_Backend.enumerations.MachineStatus;
import com.example.Machine_Management_System_Backend.enumerations.TransferStatus;
import com.example.Machine_Management_System_Backend.repositories.ConcernRepository;
import com.example.Machine_Management_System_Backend.repositories.MachineRepository;
import com.example.Machine_Management_System_Backend.repositories.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final MachineRepository machineRepository;
    private final ConcernRepository concernRepository;

    public Transfer initiateTransfer(Long machineId, Long toConcernId) {

        Machines machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        Concern toConcern = concernRepository.findById(toConcernId)
                .orElseThrow(() -> new RuntimeException("Concern not found"));

        if(machine.getCurrentConcern().equals(toConcern)){
            throw new RuntimeException("Can not transfer current concern to current concern");
        }

        if (machine.getStatus() != MachineStatus.READY_TO_TRANSFER) {
            throw new RuntimeException("Machine is not READY_TO_TRANSFER");
        }

        Transfer transfer = new Transfer();
        transfer.setMachines(machine);
        transfer.setFromConcern(machine.getCurrentConcern());
        transfer.setToConcern(toConcern);
        transfer.setStatus(TransferStatus.INITIATED);
        transfer.setInitiatedAt(LocalDateTime.now());

        machine.setStatus(MachineStatus.TRANSFERRED);
        machineRepository.save(machine);

        return transferRepository.save(transfer);
    }

    public Transfer receiveTransfer(Long transferId) {

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        if (transfer.getStatus() != TransferStatus.INITIATED) {
            throw new RuntimeException("Transfer not initiated");
        }

        Machines machine = transfer.getMachines();
        machine.setCurrentConcern(transfer.getToConcern());
        machine.setStatus(MachineStatus.ACTIVE);
        machineRepository.save(machine);

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCompletedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    public Transfer returnMachine(Long transferId) {

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        if (transfer.getStatus() != TransferStatus.COMPLETED) {
            throw new RuntimeException("Transfer not completed yet");
        }

        Machines machine = transfer.getMachines();
        machine.setCurrentConcern(transfer.getFromConcern());
        machine.setStatus(MachineStatus.ACTIVE);
        machineRepository.save(machine);

        transfer.setStatus(TransferStatus.RETURNED);
        transfer.setReturnedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    public void deleteTransfer(Long id) {
        transferRepository.deleteById(id);
    }
}