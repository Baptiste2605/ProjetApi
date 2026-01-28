package com.example.ProjetBatch.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="work_log")
public class WorkLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long employeeId;

    @Column(nullable=false)
    private LocalDate workDate;

    @Column(nullable=false)
    private double hours;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public java.time.LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(java.time.LocalDate workDate) { this.workDate = workDate; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

}
