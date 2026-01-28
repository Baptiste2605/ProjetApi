package com.example.ProjetBatch.model;
 
import jakarta.persistence.*;
import java.time.LocalDate;
 
@Entity
@Table(
    name = "hours_declaration",
    uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "month"})
)
public class HoursDeclaration {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
 
    @Column(name = "month", nullable = false)
    private LocalDate month;
 
    @Column(name = "week1")
    private Double week1;
 
    @Column(name = "week2")
    private Double week2;
 
    @Column(name = "week3")
    private Double week3;
 
    @Column(name = "week4")
    private Double week4;
 
    @Column(name = "total_hours", nullable = false)
    private Double totalHours;
 
    public Long getId() { return id; }
 
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
 
    public LocalDate getMonth() { return month; }
    public void setMonth(LocalDate month) { this.month = month; }
 
    public Double getWeek1() { return week1; }
    public void setWeek1(Double week1) { this.week1 = week1; }
 
    public Double getWeek2() { return week2; }
    public void setWeek2(Double week2) { this.week2 = week2; }
 
    public Double getWeek3() { return week3; }
    public void setWeek3(Double week3) { this.week3 = week3; }
 
    public Double getWeek4() { return week4; }
    public void setWeek4(Double week4) { this.week4 = week4; }
 
    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }
}
 
 