package com.example.ProjetApi.service;

import com.example.ProjetApi.model.Employee;
import com.example.ProjetApi.model.LeaveRequest;
import com.example.ProjetApi.repository.EmployeeRepository;
import com.example.ProjetApi.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository repo;
    private final EmployeeRepository employeeRepo;

    public LeaveRequestService(LeaveRequestRepository repo, EmployeeRepository employeeRepo) {
        this.repo = repo;
        this.employeeRepo = employeeRepo;
    }

    public List<LeaveRequest> getAll() {
        return repo.findAll();
    }

    public List<LeaveRequest> getByEmployee(Long empId) {
        return repo.findByEmployee_Id(empId);
    }

    public LeaveRequest getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Demande introuvable : " + id));
    }

    public LeaveRequest create(LeaveRequest req) {
        if (req.getEmployee() == null && req.getEmployeeId() != null) {
            Employee e = employeeRepo.findById(req.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employé introuvable avec l'ID " + req.getEmployeeId()));
            req.setEmployee(e);
        }

        // On force un statut propre à la création
        req.setStatus("EN_ATTENTE");

        return repo.save(req);
    }

    public LeaveRequest update(Long id, LeaveRequest in) {
        LeaveRequest existing = repo.findById(id).orElseThrow();

        // 1. Mise à jour des infos de base
        if (in.getStartDate() != null) existing.setStartDate(in.getStartDate());
        if (in.getEndDate() != null) existing.setEndDate(in.getEndDate());
        if (in.getReason() != null) existing.setReason(in.getReason());
        if (in.getType() != null) existing.setType(in.getType());
        
        // 2. LOGIQUE DE VALIDATION ROBUSTE (Anti-Bug encodage)
        if (in.getStatus() != null && !in.getStatus().isEmpty()) {
            
            // On récupère le statut brut envoyé par le front (ex: "VALID╔")
            String rawStatus = in.getStatus().toUpperCase(); 
            String cleanStatus = existing.getStatus(); // Par défaut on garde l'ancien

            // DÉTECTION : On regarde si ça COMMENCE par le mot clé, peu importe la suite
            if (rawStatus.startsWith("VALID")) {
                cleanStatus = "VALID";
            } else if (rawStatus.startsWith("REFUS")) {
                cleanStatus = "REFUS";
            } else if (rawStatus.startsWith("EN_ATTENTE")) {
                cleanStatus = "EN_ATTENTE";
            }

            // Si on passe à "VALID" proprement (et qu'on ne l'était pas déjà)
            if ("VALID".equals(cleanStatus) && !"VALID".equals(existing.getStatus())) {
                
                int days = calculateWorkingDays(existing.getStartDate(), existing.getEndDate());
                Employee emp = existing.getEmployee();

                if ("CP".equalsIgnoreCase(existing.getType())) {
                    emp.setSoldeCP(emp.getSoldeCP() - days);
                } else if ("RTT".equalsIgnoreCase(existing.getType())) {
                    emp.setSoldeRTT(emp.getSoldeRTT() - days);
                }
                
                employeeRepo.save(emp);
                System.out.println("✅ Congés validés (Nettoyé) pour " + emp.getLastName() + " : -" + days + " jours");
            }
            
            // IMPORTANT : On sauvegarde le statut PROPRE ("VALID") et non celui avec le symbole
            existing.setStatus(cleanStatus);
        }

        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Calcul des jours ouvrés (Sans Samedi/Dimanche)
    private int calculateWorkingDays(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0;
        if (end.isBefore(start)) return 0;

        int workingDays = 0;
        LocalDate date = start;
        
        while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            date = date.plusDays(1);
        }
        return workingDays;
    }
}