// src/main/java/com/example/ProjetApi/repository/HoursDeclarationRepository.java
package com.example.ProjetApi.repository;
 
import com.example.ProjetApi.model.HoursDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
 
public interface HoursDeclarationRepository extends JpaRepository<HoursDeclaration, Long> {
    Optional<HoursDeclaration> findByEmployee_IdAndMonth(Long employeeId, LocalDate month);
    List<HoursDeclaration> findByEmployee_IdOrderByMonthDesc(Long employeeId);
    void deleteByEmployeeId(Long id);
}
 
 