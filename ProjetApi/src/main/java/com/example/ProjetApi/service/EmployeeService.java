package com.example.ProjetApi.service;

import com.example.ProjetApi.model.Employee;
import com.example.ProjetApi.model.Poste;
import com.example.ProjetApi.repository.EmployeeRepository;
import com.example.ProjetApi.repository.HoursDeclarationRepository;
import com.example.ProjetApi.repository.LeaveRequestRepository;
import com.example.ProjetApi.repository.PayslipRepository;
import com.example.ProjetApi.repository.PosteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repo;
    private final PosteRepository posteRepo;
    private final PayslipRepository payslipRepository;
    private final LeaveRequestRepository leaveRepo;
    private final HoursDeclarationRepository hoursRepo;
    
   
    private final WelcomeEmailService emailService;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PWD_LEN = 6;
    private static final SecureRandom RNG = new SecureRandom();

    public EmployeeService(EmployeeRepository repo, 
                           PosteRepository posteRepo, 
                           PayslipRepository payslipRepository,
                           LeaveRequestRepository leaveRepo,      
                           HoursDeclarationRepository hoursRepo,
                          
                           WelcomeEmailService emailService) { 
        this.repo = repo;
        this.posteRepo = posteRepo;
        this.payslipRepository = payslipRepository;
        this.leaveRepo = leaveRepo;
        this.hoursRepo = hoursRepo;
        this.emailService = emailService; 
    }

    public List<Employee> getAll() {
        return repo.findAll();
    }

    public Employee getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Employee create(Employee e) {
        if (e.getPoste() == null && e.getPosteId() != null) {
            Poste p = posteRepo.findById(e.getPosteId())
                    .orElseThrow(() -> new RuntimeException("Poste introuvable avec l'ID " + e.getPosteId()));
            e.setPoste(p);
        }

        // Gestion du mot de passe par défaut
        if (e.getPassword() == null || e.getPassword().isEmpty()) {
            e.setPassword(generatePassword(PWD_LEN));
        }

        String clearPassword = e.getPassword();

        e.setSoldeCP(20);
        e.setSoldeRTT(10);

        Employee saved = repo.save(e);

        if (saved != null) {
            emailService.sendAccountInfo(saved, clearPassword);
        }

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        // 1. On supprime les congés
        leaveRepo.deleteByEmployee_Id(id);

        // 2. On supprime les heures
        hoursRepo.deleteByEmployeeId(id);

        // 3. On supprime les fiches de paie
        payslipRepository.deleteByEmployee_Id(id);

        // 4. Enfin, on supprime l'employé
        repo.deleteById(id);
    }

    public Employee update(Long id, Employee in) {
        Employee e = repo.findById(id).orElseThrow();
        e.setFirstName(in.getFirstName());
        e.setLastName(in.getLastName());
        e.setEmail(in.getEmail());
        
        if (in.getPoste() != null) {
            e.setPoste(in.getPoste());
        } else if (in.getPosteId() != null) {
             Poste p = posteRepo.findById(in.getPosteId()).orElse(null);
             if (p != null) e.setPoste(p);
        }
        
        return repo.save(e);
    }

    private String generatePassword(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(RNG.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}