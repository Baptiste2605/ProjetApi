package com.example.ProjectApp.gateway;

import com.example.ProjectApp.model.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
@ConditionalOnProperty(name = "app.gateway", havingValue = "mock")
public class MockGateway implements ApiGateway {

    // Stockage mémoire vide
    private final Map<Long, EmployeeDto> employees = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public MockGateway() {
        System.out.println(">>> Using MockGateway (Empty Mode)");
    }

    // --- EMPLOYEES ---
    @Override
    public List<EmployeeDto> listEmployees() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto e) {
        long id = seq.incrementAndGet();
        e.setId(id);
        employees.put(id, e);
        return e;
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto e) {
        if (employees.containsKey(id)) {
            e.setId(id);
            employees.put(id, e);
            return e;
        }
        return null;
    }

    @Override
    public void deleteEmployee(Long id) {
        employees.remove(id);
    }

    @Override
    public EmployeeDto getEmployee(Long id) {
        return employees.get(id);
    }

    // --- PAYSLIPS ---
    @Override
    public List<PayslipDto> listPayslipsByEmployee(Long employeeId) {
        return new ArrayList<>();
    }

    @Override
    public ResponseEntity<byte[]> downloadPayslip(Long payslipId) {
        return ResponseEntity.notFound().build();
    }

    // --- POSTES ---
    @Override
    public List<PosteDto> listPoste() {
        return new ArrayList<>();
    }

    // --- AUTH ---
    @Override
    public EmployeeDto login(String email, String password) {
        // Retourne null car aucun utilisateur n'existe
        return null;
    }

    // --- HOURS ---
    @Override
    public void upsertHours(Long employeeId, LocalDate month, Double w1, Double w2, Double w3, Double w4) {
        // Ne fait rien
    }

    // --- LEAVES (CONGÉS) ---
    @Override
    public List<LeaveRequestDto> listLeaves(Long employeeId) {
        return new ArrayList<>();
    }

    @Override
    public LeaveRequestDto createLeave(Long employeeId, LeaveRequestDto dto) {
        return dto;
    }

    @Override
    public List<LeaveRequestDto> listAllLeaves() {
        return new ArrayList<>();
    }

    @Override
    public void updateLeaveStatus(Long id, String status) {
       
    }
}