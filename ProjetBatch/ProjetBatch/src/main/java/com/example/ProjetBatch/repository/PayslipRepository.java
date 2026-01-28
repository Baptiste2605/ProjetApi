package com.example.ProjetBatch.repository;

import com.example.ProjetBatch.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {


    boolean existsByEmployee_IdAndMonth(Long employeeId, LocalDate month);

    
}
