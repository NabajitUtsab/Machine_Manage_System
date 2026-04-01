package com.example.Machine_Management_System_Backend.repositories;

import com.example.Machine_Management_System_Backend.entity.Machines;
import com.example.Machine_Management_System_Backend.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByMachines(Machines machine);
    List<Transfer> findByToConcern_Id(Long concernId);
    List<Transfer> findByFromConcern_Id(Long concernId);
}
