package com.example.Machine_Management_System_Backend.repositories;

import com.example.Machine_Management_System_Backend.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ConcernRepository extends JpaRepository<Concern, Long> {

    Optional<Concern> findConcernByName(String name);
}
