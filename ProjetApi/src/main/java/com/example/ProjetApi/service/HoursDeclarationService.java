// src/main/java/com/example/ProjetApi/service/HoursDeclarationService.java
package com.example.ProjetApi.service;
 
import com.example.ProjetApi.model.Employee;
import com.example.ProjetApi.model.HoursDeclaration;
import com.example.ProjetApi.repository.EmployeeRepository;
import com.example.ProjetApi.repository.HoursDeclarationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
 
@Service
public class HoursDeclarationService {
 
    private final HoursDeclarationRepository repo;
    private final EmployeeRepository employeeRepo;
 
    public HoursDeclarationService(HoursDeclarationRepository repo, EmployeeRepository employeeRepo) {
        this.repo = repo;
        this.employeeRepo = employeeRepo;
    }
 
    @Transactional
    public HoursDeclaration upsert(Long employeeId,
                                   LocalDate month, 
                                   Double w1, Double w2, Double w3, Double w4) {
 
        Employee emp = employeeRepo.findById(employeeId).orElseThrow();
 
        LocalDate m = month.withDayOfMonth(1); // normalise
        double total = (w1==null?0:w1) + (w2==null?0:w2) + (w3==null?0:w3) + (w4==null?0:w4);
 
        HoursDeclaration hd = repo.findByEmployee_IdAndMonth(employeeId, m)
                .orElseGet(() -> {
                    HoursDeclaration x = new HoursDeclaration();
                    x.setEmployee(emp);
                    x.setMonth(m);
                    return x;
                });
 
        hd.setWeek1(w1);
        hd.setWeek2(w2);
        hd.setWeek3(w3);
        hd.setWeek4(w4);
        hd.setTotalHours(total);
 
        return repo.save(hd);
    }
 
    public List<HoursDeclaration> listByEmployee(Long employeeId) {
        return repo.findByEmployee_IdOrderByMonthDesc(employeeId);
    }
}
 
 