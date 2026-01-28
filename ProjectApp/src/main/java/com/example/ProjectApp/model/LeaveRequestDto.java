package com.example.ProjectApp.model;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

public class LeaveRequestDto {

    private Long id;
    private Long employeeId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String type;

    private String reason;

    private String status;

    public LeaveRequestDto() {

        this.status = "EN_ATTENTE";

    }

    public LeaveRequestDto(Long id, LocalDate startDate, LocalDate endDate, String type, String status) {

        this.id = id;

        this.startDate = startDate;

        this.endDate = endDate;

        this.type = type;

        this.status = status;

    }

    public long getDaysCount() {

        if (startDate == null || endDate == null) return 0;

        return ChronoUnit.DAYS.between(startDate, endDate) + 1;

    }

    // Getters & Setters standard
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

}
