package com.example.Machine_Management_System_Backend.entity;

import com.example.Machine_Management_System_Backend.enumerations.TransferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfer")
@Builder
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Machines machines;

    @ManyToOne
    private Concern fromConcern;

    @ManyToOne
    private Concern toConcern;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime returnedAt;
}
