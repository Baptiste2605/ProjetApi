package com.example.ProjectApp.controller;

import com.example.ProjectApp.gateway.ApiGateway;
import com.example.ProjectApp.model.EmployeeDto;
import com.example.ProjectApp.model.HoursDeclarationDto;
import com.example.ProjectApp.model.LeaveRequestDto;
import com.example.ProjectApp.model.NewsDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class MeController {

    private final ApiGateway api;

    public MeController(ApiGateway api) {
        this.api = api;
    }

    // -----------------------------------------------------------------
    // 1. GESTION DES BULLETINS
    // -----------------------------------------------------------------
    @GetMapping("/me/payslips")
    public String myPayslips(Model m, Authentication auth) {
        // CORRECTION : On récupère l'ID via le login, pas via l'URL
        Long employeeId = resolveEmployeeIdFromAuth(auth);

        m.addAttribute("active", "me");
        m.addAttribute("pageTitle", "Mes bulletins");
        m.addAttribute("employeeId", employeeId);
        m.addAttribute("payslips", api.listPayslipsByEmployee(employeeId));

        return "me-payslips";
    }

    @GetMapping("/me/payslips/{id}/download")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long id) {
        return api.downloadPayslip(id);
    }

    // -----------------------------------------------------------------
    // 2. GESTION DES HEURES
    // -----------------------------------------------------------------
    @GetMapping("/me/hours")
    public String myHours(Model m, Authentication auth) {
        // CORRECTION : Simplification pour utiliser toujours l'Auth
        Long employeeId = resolveEmployeeIdFromAuth(auth);
        
        LocalDate month = LocalDate.now().withDayOfMonth(1);

        m.addAttribute("activeTab", "hours");
        m.addAttribute("pageTitle", "Mes Heures");
        m.addAttribute("employeeId", employeeId);
        m.addAttribute("month", month);
        m.addAttribute("submittedTotal", 140.0); 
        return "me-hours";
    }

    @PostMapping("/me/hours")
    public String submitHours(@RequestParam("month") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month,
                              @RequestParam("week1") Double week1,
                              @RequestParam("week2") Double week2,
                              @RequestParam("week3") Double week3,
                              @RequestParam("week4") Double week4,
                              Model m,
                              Authentication auth) { // Ajout de Authentication

        // CORRECTION : On ignore l'ID du formulaire, on prend celui du token de sécurité
        Long employeeId = resolveEmployeeIdFromAuth(auth);

        double total = (week1 == null ? 0 : week1)
                     + (week2 == null ? 0 : week2)
                     + (week3 == null ? 0 : week3)
                     + (week4 == null ? 0 : week4);

        api.upsertHours(employeeId, month, week1, week2, week3, week4);

        m.addAttribute("activeTab", "hours");
        m.addAttribute("pageTitle", "Mes Heures");
        m.addAttribute("employeeId", employeeId);
        m.addAttribute("month", month);
        m.addAttribute("submittedTotal", total);
        m.addAttribute("successMessage", "Déclaration enregistrée (" + month + ") - Total: " + total + " h.");
        return "me-hours";
    }

    // -----------------------------------------------------------------
    // 3. GESTION DES CONGÉS (C'est ici que ça bloquait !)
    // -----------------------------------------------------------------
    @GetMapping("/me/leaves")
    public String myLeaves(Model m, Authentication auth) {
        // CORRECTION : Plus de @RequestParam avec defaultValue="1" !
        Long employeeId = resolveEmployeeIdFromAuth(auth);

        m.addAttribute("activeTab", "leaves");
        m.addAttribute("pageTitle", "Mes Congés");

        // 1. Récupération des infos (soldes)
        EmployeeDto employee = api.getEmployee(employeeId);
        m.addAttribute("soldeCP", employee.getSoldeCP());
        m.addAttribute("soldeRTT", employee.getSoldeRTT());

        // 2. Liste des demandes
        m.addAttribute("leaveRequest", new LeaveRequestDto());
        m.addAttribute("leaves", api.listLeaves(employeeId));

        return "me-leaves";
    }

    @PostMapping("/me/leaves")
    public String submitLeave(@ModelAttribute("leaveRequest") LeaveRequestDto form,
                              Model m,
                              Authentication auth) { // On injecte l'identité
        
        // CORRECTION MAJEURE ICI : On détermine l'ID grâce à la connexion
        Long employeeId = resolveEmployeeIdFromAuth(auth);

        // Appel API
        api.createLeave(employeeId, form);

        // Rechargement de la page
        m.addAttribute("activeTab", "leaves");
        m.addAttribute("pageTitle", "Mes Congés");
        m.addAttribute("successMessage", "Votre demande a été enregistrée !");
        
        // Mise à jour de l'affichage
        EmployeeDto employee = api.getEmployee(employeeId);
        m.addAttribute("soldeCP", employee.getSoldeCP());   // Vrais soldes
        m.addAttribute("soldeRTT", employee.getSoldeRTT()); // Vrais soldes
        
        m.addAttribute("leaves", api.listLeaves(employeeId)); // Liste à jour
        m.addAttribute("leaveRequest", new LeaveRequestDto());

        return "me-leaves";
    }

    // -----------------------------------------------------------------
    // 4. NEWS
    // -----------------------------------------------------------------
    @GetMapping("/me/news")
    public String myNews(Model m) {
        m.addAttribute("activeTab", "news");
        m.addAttribute("pageTitle", "Infos Groupe");

        List<NewsDto> newsList = Arrays.asList(
                new NewsDto("Révolution IA & Quantique ! ⚛️", LocalDate.now(), "Innovation",
                        "Incroyable ! Nos collaborateurs Marius Babin et Baptiste Durand ont révolutionné le monde...",
                        "danger", "/images/chamipion.jpg"),
                new NewsDto("Grosse Vente signée ! ", LocalDate.now().minusDays(2), "Business",
                        "Félicitations à l'équipe commerciale.", "success", null),
                new NewsDto("Petit-déj Croissants ", LocalDate.now().minusDays(5), "Vie de bureau",
                        "Merci à Julie pour les croissants.", "warning", null)
        );

        m.addAttribute("newsList", newsList);
        return "me-news";
    }

    // -----------------------------------------------------------------
    // UTILITAIRE (Récupère l'ID depuis l'email du login)
    // -----------------------------------------------------------------
    private Long resolveEmployeeIdFromAuth(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            // Sécurité : si pas connecté, on renvoie null (ou on pourrait lancer une exception)
            // Pour le dev, si pas connecté, on peut fallback sur 1, mais idéalement non.
            return 1L; 
        }
        String email = auth.getName();
        return api.listEmployees().stream()
                .filter(e -> e.getEmail() != null && e.getEmail().equalsIgnoreCase(email))
                .map(EmployeeDto::getId)
                .findFirst()
                .orElse(1L); // Fallback sur 1 si email non trouvé en base (cas de test)
    }
}