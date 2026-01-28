package com.example.ProjetBatch.repository;

import com.example.ProjetBatch.model.HoursDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface HoursDeclarationRepository extends JpaRepository<HoursDeclaration, Long> {
    
    Optional<HoursDeclaration> findByEmployeeIdAndMonth(Long employeeId, LocalDate month);
}