package com.example.ProjectApp.controller;

import com.example.ProjectApp.gateway.ApiGateway;

import com.example.ProjectApp.model.EmployeeDto;
import com.example.ProjectApp.model.LeaveRequestDto;
import com.example.ProjectApp.model.PayslipDto;
import com.example.ProjectApp.model.PosteDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("/rh")
public class RhController {

    private final ApiGateway api;


    // INJECTION VIA LE CONSTRUCTEUR (On injecte les deux gateways)
    public RhController(ApiGateway api) {
        this.api = api;
    }

    @GetMapping("/employees")
    public String employees(@RequestParam(value = "editId", required = false) Long editId,
                            @RequestParam(value = "q", required = false) String q,
                            Model m) {

        m.addAttribute("active", "rh");
        m.addAttribute("pageTitle", "Espace RH");

        // employees
        var list = api.listEmployees();

        // optional search
        if (q != null && !q.isBlank()) {
            final String qs = q.trim().toLowerCase();
            final Long idQ = qs.matches("\\d+") ? Long.valueOf(qs) : null;

            list = list.stream()
                    .filter(e ->
                            (idQ != null && e.getId() != null && e.getId().equals(idQ)) ||
                                    (e.getFirstName() != null && e.getFirstName().toLowerCase().contains(qs)) ||
                                    (e.getLastName()  != null && e.getLastName().toLowerCase().contains(qs)) ||
                                    (e.getEmail()     != null && e.getEmail().toLowerCase().contains(qs))
                    )
                    .toList();
            m.addAttribute("q", q);
        } else {
            m.addAttribute("q", "");
        }


        list = list.stream().sorted(Comparator.comparing(EmployeeDto::getId)).toList();
        m.addAttribute("employees", list);
        

        var poste = api.listPoste();      
        m.addAttribute("postes", poste);   

        boolean editMode = false;
        EmployeeDto form = new EmployeeDto();

        if (editId != null) {
            Optional<EmployeeDto> toEdit = list.stream()
                    .filter(e -> e.getId() != null && e.getId().equals(editId))
                    .findFirst();
            if (toEdit.isPresent()) {
                form = toEdit.get();
                editMode = true;
            }
        }

        m.addAttribute("form", form);
        m.addAttribute("editMode", editMode);

        m.addAttribute("showPayslips", false);

        return "rh-employees";
    }

    // === CREATE ===
    @PostMapping("/employees")
    public String create(@ModelAttribute("form") EmployeeDto form, RedirectAttributes ra) {
        api.createEmployee(form);
        ra.addFlashAttribute("success", "Employe cree");
        return "redirect:/rh/employees";
    }

    // === UPDATE ===
    @PostMapping("/employees/{id}/update")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") EmployeeDto form,
                         RedirectAttributes ra) {
        form.setId(id);
        api.updateEmployee(id, form);
        ra.addFlashAttribute("success", "Employe mis a jour");
        return "redirect:/rh/employees";
    }

    // === DELETE ===
    @PostMapping("/employees/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra){
        api.deleteEmployee(id);
        ra.addFlashAttribute("success", "Employe supprime");
        return "redirect:/rh/employees";
    }

 
    @GetMapping("/employees/{id}/payslips")
    public String viewPayslips(@PathVariable("id") Long employeeId,
                               @RequestParam(value = "editId", required = false) Long editId,
                               @RequestParam(value = "q", required = false) String q,
                               Model m) {

        m.addAttribute("active", "rh");
        m.addAttribute("pageTitle", "Espace RH");

        // employees + same search for UI coherence
        var employees = api.listEmployees();

        if (q != null && !q.isBlank()) {
            final String qs = q.trim().toLowerCase();
            final Long idQ = qs.matches("\\d+") ? Long.valueOf(qs) : null;

            employees = employees.stream()
                    .filter(e ->
                            (idQ != null && e.getId() != null && e.getId().equals(idQ)) ||
                                    (e.getFirstName() != null && e.getFirstName().toLowerCase().contains(qs)) ||
                                    (e.getLastName()  != null && e.getLastName().toLowerCase().contains(qs)) ||
                                    (e.getEmail()     != null && e.getEmail().toLowerCase().contains(qs))
                    )
                    .toList();

            m.addAttribute("q", q);
        } else {
            m.addAttribute("q", "");
        }

        employees = employees.stream().sorted(Comparator.comparing(EmployeeDto::getId)).toList();
        m.addAttribute("employees", employees);

        // --- add postes from DB for the form on this view too ---
        List<PosteDto> postes = api.listPoste();
        m.addAttribute("postes", postes);

        // form state
        boolean editMode = false;
        EmployeeDto form = new EmployeeDto();

        if (editId != null) {
            Optional<EmployeeDto> toEdit = employees.stream()
                    .filter(e -> e.getId() != null && e.getId().equals(editId))
                    .findFirst();
            if (toEdit.isPresent()) {
                form = toEdit.get();
                editMode = true;
            }
        }

        m.addAttribute("form", form);
        m.addAttribute("editMode", editMode);

        // selected employee (identity)
        Optional<EmployeeDto> selectedOpt = api.listEmployees().stream()
                .filter(e -> e.getId() != null && e.getId().equals(employeeId))
                .findFirst();
        selectedOpt.ifPresent(se -> m.addAttribute("selectedEmployee", se));

        // payslips (fallback demo if empty)
        List<PayslipDto> payslips = api.listPayslipsByEmployee(employeeId);


        m.addAttribute("showPayslips", true);
        m.addAttribute("selectedEmployeeId", employeeId);
        m.addAttribute("selectedEmployeeName",
                selectedOpt.map(e -> e.getFirstName() + " " + e.getLastName())
                        .orElse("Employe #" + employeeId));
        m.addAttribute("payslips", payslips);

        return "rh-employees";
    }

    @GetMapping("/payslips/{id}/download")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable("id") Long id) {
        var resp = api.downloadPayslip(id); // appelle l'API
        if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return ResponseEntity.notFound().build();
        }
        // on propage Content-Type et Content-Disposition si présents
        String ct = resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        String cd = resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, (ct != null ? ct : MediaType.APPLICATION_PDF_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, (cd != null ? cd : "attachment; filename=\"payslip.pdf\""))
                .body(resp.getBody());
    }

    // --- GESTION DES CONGÉS (Espace RH) ---

    @GetMapping("/leaves")
    public String viewLeaves(Model m) {
        m.addAttribute("active", "leaves"); // Pour l'onglet actif
        m.addAttribute("pageTitle", "Gestion des Congés");
        
        // On récupère TOUTES les demandes de la boite
        List<LeaveRequestDto> allLeaves = api.listAllLeaves();
        
        // On peut les trier : les "EN_ATTENTE" en premier
        allLeaves.sort((l1, l2) -> {
            if ("EN_ATTENTE".equals(l1.getStatus()) && !"EN_ATTENTE".equals(l2.getStatus())) return -1;
            if (!"EN_ATTENTE".equals(l1.getStatus()) && "EN_ATTENTE".equals(l2.getStatus())) return 1;
            return l2.getStartDate().compareTo(l1.getStartDate()); // Puis par date
        });

        m.addAttribute("leaves", allLeaves);
        
        return "rh-leaves";
    }

    @PostMapping("/leaves/{id}/update")
    public String updateLeaveStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes ra) {
        api.updateLeaveStatus(id, status);
        
        String msg = status.equals("VALID") ? "Demande validée !" : "Demande refusée.";
        ra.addFlashAttribute("success", msg);
        
        return "redirect:/rh/leaves";
    }



}