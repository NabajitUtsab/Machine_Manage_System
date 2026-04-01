package com.example.Machine_Management_System_Backend.controller;

import com.example.Machine_Management_System_Backend.entity.Concern;
import com.example.Machine_Management_System_Backend.entity.Transfer;
import com.example.Machine_Management_System_Backend.service.ConcernService;
import com.example.Machine_Management_System_Backend.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final ConcernService concernService;

    @PostMapping("/initiate")
    public Transfer initiate(@RequestParam Long machineId, @RequestParam String toConcernName) {
        Concern toConcern = concernService.getConcernByName(toConcernName);
        return transferService.initiateTransfer(machineId, toConcern.getId());
    }

    @PostMapping("/receive")
    public Transfer receive(@RequestParam Long transferId) {
        return transferService.receiveTransfer(transferId);
    }

    @PostMapping("/return")
    public Transfer returnMachine(@RequestParam Long transferId) {
        return transferService.returnMachine(transferId);
    }
}