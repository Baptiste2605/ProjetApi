package com.example.ProjetBatch.controller;
 
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
 
@Controller
@RequestMapping("/rh/payslips")
public class JobLauncherController {
 
    private final JobLauncher jobLauncher;
    private final Job generatePayslipsJob;
 
    // Injection des beans Batch
    public JobLauncherController(JobLauncher jobLauncher,
                                 @Qualifier("generatePayslipsJob") Job generatePayslipsJob) {
        this.jobLauncher = jobLauncher;
        this.generatePayslipsJob = generatePayslipsJob;
    }
 
    @PostMapping("/generate")
    public RedirectView runBatchManually() {
        try {
            // L'astuce est là : on ajoute le temps actuel ("time") aux paramètres.
            // Spring Batch considère ainsi que c'est une NOUVELLE exécution différente de la précédente.
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
 
            System.out.println("🔘 Bouton pressé : Lancement manuel du Batch...");
            jobLauncher.run(generatePayslipsJob, params);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        // On redirige l'utilisateur vers la liste des employés après le clic
        return new RedirectView("/rh/employees");
    }
}
 