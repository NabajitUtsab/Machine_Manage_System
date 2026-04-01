package com.example.Machine_Management_System_Backend.service;

import com.example.Machine_Management_System_Backend.entity.Concern;
import com.example.Machine_Management_System_Backend.repositories.ConcernRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcernService {

    private final ConcernRepository concernRepository;

    public Concern createConcern(String name) {
        Concern concern = new Concern();
        concern.setName(name);
        return concernRepository.save(concern);
    }

    public Concern getConcernByName(String name) {
        return concernRepository.findConcernByName(name)
                .orElseThrow(() -> new RuntimeException("Concern not found: " + name));
    }

    public Concern getConcernById(Long id) {
        return concernRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concern not found with ID: " + id));
    }

    public Concern updateConcern(Long id, String concernName) {
        Concern existedConcern = getConcernById(id);
        existedConcern.setName(concernName);
        return concernRepository.save(existedConcern);
    }

    public void deleteConcern(Long id) {
        concernRepository.deleteById(id);
    }

    public List<Concern> getAllConcerns() {
        return concernRepository.findAll();
    }
}