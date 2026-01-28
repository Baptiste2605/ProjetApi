package com.example.ProjetBatch.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payslip")
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // lien vers l'employé payé
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * Mois concerné par le bulletin.
     * Convention: on stocke le 1er jour du mois (ex: 2025-10-01 pour Octobre 2025)
     */
    @Column(nullable = false)
    private LocalDate month;

    /**
     * Total d'heures travaillées sur le mois (figé au moment du calcul)
     */
    @Column(name = "total_hours", nullable = false)
    private double totalHours;

    /**
     * Taux horaire appliqué (€/h) au moment du calcul
     * -> vient du poste de l'employé à cet instant
     */
    @Column(name = "hourly_rate", nullable = false)
    private double hourlyRate;

    /**
     * Salaire brut calculé = totalHours * hourlyRate
     */
    @Column(nullable = false)
    private double gross;

    /**
     * Salaire net calculé (ex: brut * 0.78)
     */
    @Column(nullable = false)
    private double net;


    @Column(nullable = false)
    private String status;


    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(length = 1000) // On prévoit de la place pour du texte
    private String leavesInfo;

    public String getLeavesInfo() {
        return leavesInfo;
    }

    public void setLeavesInfo(String leavesInfo) {
        this.leavesInfo = leavesInfo;
    }

    public Payslip() {}

    public Payslip(Employee employee,
                   LocalDate month,
                   double totalHours,
                   double hourlyRate,
                   double gross,
                   double net,
                   String status) {
        this.employee = employee;
        this.month = month;
        this.totalHours = totalHours;
        this.hourlyRate = hourlyRate;
        this.gross = gross;
        this.net = net;
        this.status = status;
    }

    // ---------- getters / setters ----------

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getMonth() {
        return month;
    }
    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public double getTotalHours() {
        return totalHours;
    }
    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getGross() {
        return gross;
    }
    public void setGross(double gross) {
        this.gross = gross;
    }

    public double getNet() {
        return net;
    }
    public void setNet(double net) {
        this.net = net;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // ---------- helpers non persistés ----------

    /**
     * Id employé à renvoyer facilement au front / API JSON.
     * Pas stocké en base.
     */
    @Transient
    public Long getEmployeeId() {
        return (employee != null ? employee.getId() : null);
    }
}