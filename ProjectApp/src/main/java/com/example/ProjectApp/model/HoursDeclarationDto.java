package com.example.ProjectApp.model;
 
import org.springframework.format.annotation.DateTimeFormat;
 
import java.time.LocalDate;

 
public class HoursDeclarationDto {
 
    // identifiant de l'employé qui déclare
    private Long employeeId;
 
    // mois concerné (type month => "yyyy-MM"), Spring le lie à YearMonth avec l'annotation
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate month;
 
    private Double week1;
    private Double week2;
    private Double week3;
    private Double week4;
 
    // calculé/affiché
    private Double totalHours;
 
    public HoursDeclarationDto() {
        this.month  = LocalDate.now().withDayOfMonth(1); // mois courant par défaut
        this.week1  = 35.0;
        this.week2  = 35.0;
        this.week3  = 35.0;
        this.week4  = 35.0;
        this.totalHours = 140.0;       // 4 * 35
    }
 
    // utilitaire pratique si tu veux recalculer côté serveur
    public void recomputeTotal() {
        double w1 = week1 == null ? 0 : week1;
        double w2 = week2 == null ? 0 : week2;
        double w3 = week3 == null ? 0 : week3;
        double w4 = week4 == null ? 0 : week4;
        this.totalHours = w1 + w2 + w3 + w4;
    }
 
    // getters/setters
 
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
 
 