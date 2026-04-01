package com.example.Machine_Management_System_Backend.entity;

import com.example.Machine_Management_System_Backend.enumerations.MachineStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "machines")
public class Machines {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;
    private String groupName;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    @ManyToOne
    private Concern originConcern;

    @ManyToOne
    @JoinColumn(name = "current_concern_id")
    private Concern currentConcern;

    private String qrCodePath;
}
