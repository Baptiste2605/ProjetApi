package com.example.ProjetBatch.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String type;    // RTT, CP, MALADIE...
    private String status;  // VALID, EN_ATTENTE...
    
    // On a juste besoin de l'ID de l'employé pour le lier
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Getters
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Employee getEmployee() { return employee; }
}