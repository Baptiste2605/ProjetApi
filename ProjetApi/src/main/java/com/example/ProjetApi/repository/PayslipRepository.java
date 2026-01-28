package com.example.ProjetApi.repository; // Vérifie que le package est bon

import com.example.ProjetApi.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    
    // Lister les fiches de paie d'un employé
    List<Payslip> findByEmployee_Id(Long employeeId);

    // --- AJOUT INDISPENSABLE POUR LE BATCH ---
    // Vérifie si une fiche existe déjà pour ce mois (évite les doublons)
    boolean existsByEmployee_IdAndMonth(Long id, LocalDate month);

    // --- CORRECTION SYNTAXE ---
    // On met un "_" pour bien cibler l'ID à l'intérieur de l'objet Employee
    void deleteByEmployee_Id(Long id);
}