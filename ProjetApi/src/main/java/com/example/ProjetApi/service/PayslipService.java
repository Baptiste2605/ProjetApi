package com.example.ProjetApi.service;



import com.example.ProjetApi.model.Employee;
import com.example.ProjetApi.model.Payslip;
import com.example.ProjetApi.repository.EmployeeRepository;
import com.example.ProjetApi.repository.PayslipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PayslipService {
    private final PayslipRepository payslipRepo;
    private final EmployeeRepository employeeRepo;

    public PayslipService(PayslipRepository payslipRepo, EmployeeRepository employeeRepo) {
        this.payslipRepo = payslipRepo; this.employeeRepo = employeeRepo;
    }

    public List<Payslip> listByEmployee(Long employeeId) {
        return payslipRepo.findByEmployee_Id(employeeId);
    }

    // utilitaire si tu veux insérer quelques lignes de test
    public Payslip create(
            Long employeeId,
            LocalDate month,
            double totalHours,
            double hourlyRate,
            double gross,
            double net,
            String status,
            String fileName,
            String filePath
    ) {
        Employee e = employeeRepo.findById(employeeId).orElseThrow();

        Payslip p = new Payslip();
        p.setEmployee(e);
        p.setMonth(month.withDayOfMonth(1));
        p.setTotalHours(totalHours);
        p.setHourlyRate(hourlyRate);
        p.setGross(gross);
        p.setNet(net);
        p.setStatus(status);
        p.setFileName(fileName);
        p.setFilePath(filePath);

        return payslipRepo.save(p);
    }
    public Payslip getById(Long id) {
        return payslipRepo.findById(id).orElse(null);
    }
}
