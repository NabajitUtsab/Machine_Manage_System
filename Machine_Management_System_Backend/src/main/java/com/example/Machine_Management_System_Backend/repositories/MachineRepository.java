package com.example.Machine_Management_System_Backend.repositories;

import com.example.Machine_Management_System_Backend.entity.Machines;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machines,Long> {
    Optional<Machines> findByCode(String code);
    List<Machines> findByCurrentConcernId(Long concernId);
}
