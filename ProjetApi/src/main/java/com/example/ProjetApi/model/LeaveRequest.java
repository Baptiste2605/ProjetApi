package com.example.ProjetApi.model;

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

    private String type;    // Ex: "CP", "RTT"
    private String reason;  // Motif
    private String status;  // Ex: "EN_ATTENTE", "VALID", "REFUS"

    // --- RELATION VERS L'EMPLOYÉ (Base de données) ---
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // --- CHAMP UTILITAIRE (Pour le JSON, non stocké en base) ---
    @Transient
    private Long employeeId;

    // --- CONSTRUCTEURS ---

    public LeaveRequest() {
    }

    public LeaveRequest(LocalDate startDate, LocalDate endDate, String type, String reason, Employee employee) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reason = reason;
        this.employee = employee;
        this.status = "EN_ATTENTE";
    }

    // --- GETTERS ET SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}