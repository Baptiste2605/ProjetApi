package com.example.ProjetBatch.config;

import com.example.ProjetBatch.model.Employee;
import com.example.ProjetBatch.model.Payslip;
import com.example.ProjetBatch.model.LeaveRequest;
import com.example.ProjetBatch.repository.EmployeeRepository;
import com.example.ProjetBatch.repository.PayslipRepository;
import com.example.ProjetBatch.repository.HoursDeclarationRepository;
import com.example.ProjetBatch.repository.LeaveRequestRepository;
import com.example.ProjetBatch.service.PayslipPdfService;
import com.example.ProjetBatch.service.EmailService;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableScheduling
public class BatchConfiguration {

    // --- Repositories & Services globaux ---
    private final EmployeeRepository employeeRepo;
    private final PayslipRepository payslipRepo;
    private final PayslipPdfService pdfService;
    private final EmailService emailService;

    // --- Infrastructure Batch ---
    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private final JobLauncher jobLauncher;
    private final EntityManagerFactory emf;

    // --- Constructeur ---
    public BatchConfiguration(EmployeeRepository employeeRepo,
                              PayslipRepository payslipRepo,
                              PayslipPdfService pdfService,
                              JobRepository jobRepository,
                              PlatformTransactionManager tx,
                              JobLauncher jobLauncher,
                              EntityManagerFactory emf,
                              @Autowired(required = false) EmailService emailService) {
        this.employeeRepo = employeeRepo;
        this.payslipRepo = payslipRepo;
        this.pdfService = pdfService;
        this.jobRepository = jobRepository;
        this.tx = tx;
        this.jobLauncher = jobLauncher;
        this.emf = emf;
        this.emailService = emailService;
    }

    // =========================================================
    // ===============   JOB 1 : GÉNÉRATION PAIE   =============
    // =========================================================

    @Bean
    public JpaPagingItemReader<Employee> employeeReader() {
        JpaPagingItemReader<Employee> r = new JpaPagingItemReader<>();
        r.setEntityManagerFactory(emf);
        r.setQueryString("SELECT e FROM Employee e");
        r.setPageSize(20);
        return r;
    }

    @Bean
    public ItemProcessor<Employee, Payslip> payslipProcessor(
            HoursDeclarationRepository hoursRepo,
            PayslipRepository payslipRepo,
            LeaveRequestRepository leaveRepo 
    ) {
        return emp -> {
            System.out.println("--- Traitement de l'employé : " + emp.getFirstName() + " " + emp.getLastName() + " ---");

            if (emp.getPoste() == null) {
                System.err.println("❌ REJETÉ : Pas de poste assigné");
                return null;
            }

            LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            if (payslipRepo.existsByEmployee_IdAndMonth(emp.getId(), monthStart)) {
                // On ne rejette pas bruyamment, on ignore silencieusement pour le mode incrémental
                return null; 
            }

            // --- RECUPERATION HEURES ---
            Double totalHours;
            var declaration = hoursRepo.findByEmployeeIdAndMonth(emp.getId(), monthStart);
            if (declaration.isPresent()) {
                totalHours = declaration.get().getTotalHours();
                System.out.println("   ✅ Heures déclarées : " + totalHours);
            } else {
                totalHours = 151.67; 
                System.out.println("   ℹ️ Forfait automatique : 151.67 h");
            }

            // --- RECUPERATION CONGES ---
            List<LeaveRequest> leaves = leaveRepo.findByEmployee_Id(emp.getId());
            StringBuilder sb = new StringBuilder();
            int joursConges = 0;

            for (LeaveRequest l : leaves) {
                if ("VALID".equals(l.getStatus()) && 
                    !l.getStartDate().isAfter(monthEnd) && 
                    !l.getEndDate().isBefore(monthStart)) {
                    
                    sb.append("- ").append(l.getType())
                      .append(" : du ").append(l.getStartDate())
                      .append(" au ").append(l.getEndDate()).append("\n");
                    joursConges++;
                }
            }
            String infoConges = (joursConges > 0) ? sb.toString() : "Aucune absence ce mois-ci.";

            // --- CALCULS ---
            double hourlyRate = emp.getPoste().getHourlyRate();
            double gross = hourlyRate * totalHours;
            double net = Math.round(gross * 0.78 * 100.0) / 100.0;

            Payslip p = new Payslip();
            p.setEmployee(emp);
            p.setMonth(monthStart);
            p.setTotalHours(totalHours);
            p.setHourlyRate(hourlyRate);
            p.setGross(gross);
            p.setNet(net);
            p.setLeavesInfo(infoConges);
            p.setStatus("GENERATED");

            return p;
        };
    }

    @Bean
    public ItemWriter<Payslip> payslipWriter() {
        return payslips -> {
            for (Payslip p : payslips) {
                Payslip saved = payslipRepo.save(p);
                Employee emp = saved.getEmployee();

                try {
                    String path = pdfService.generatePdf(emp, saved);
                    File pdfFile = new File(path);

                    saved.setFileName(pdfFile.getName());
                    saved.setFilePath(path);
                    saved.setStatus("PDF_OK");

                    if (emailService != null) {
                        try {
                            emailService.sendPayslip(emp, saved, pdfFile);
                        } catch (Exception e) {
                            System.err.println("   ⚠️ Mail échoué pour " + emp.getEmail());
                            System.err.println("   👉 CAUSE : " + e.getMessage()); 
                            e.printStackTrace(); // Affiche tous les détails techniques dans les logs
                        }
                    }

                    payslipRepo.save(saved);
                    System.out.println("   ✅ Bulletin généré pour " + emp.getLastName());

                } catch (Exception ex) {
                    saved.setStatus("PDF_ERROR");
                    payslipRepo.save(saved);
                    System.err.println("   ❌ Erreur PDF pour " + emp.getLastName());
                }
            }
        };
    }

    @Bean
    public Step generatePayslipsStep(ItemReader<Employee> employeeReader,
                                     ItemProcessor<Employee, Payslip> payslipProcessor,
                                     ItemWriter<Payslip> payslipWriter) {
        return new StepBuilder("generatePayslipsStep", jobRepository)
                .<Employee, Payslip>chunk(20, tx)
                .reader(employeeReader)
                .processor(payslipProcessor)
                .writer(payslipWriter)
                .build();
    }

    @Bean
    public Job generatePayslipsJob(Step generatePayslipsStep) {
        SimpleJob job = new SimpleJob();
        job.setName("generatePayslipsJob");
        job.setJobRepository(jobRepository);
        job.addStep(generatePayslipsStep);
        return job;
    }

    // =========================================================
    // ===============   JOB 2 : EXPORT CSV        =============
    // =========================================================

    // --- LE BEAN QUI MANQUAIT PROBABLEMENT ---
    @Bean
    public JpaPagingItemReader<Payslip> payslipDbReader() {
        JpaPagingItemReader<Payslip> r = new JpaPagingItemReader<>();
        r.setEntityManagerFactory(emf);
        r.setQueryString("SELECT p FROM Payslip p ORDER BY p.month DESC, p.employee.id");
        r.setPageSize(50);
        return r;
    }

    @Bean
    public FlatFileItemWriter<Payslip> payslipCsvWriter() {
        FlatFileItemWriter<Payslip> w = new FlatFileItemWriter<>();
        w.setResource(new FileSystemResource("exported_payslips.csv"));
        w.setAppendAllowed(false);
        w.setHeaderCallback(writer -> writer.write("id,employeeId,month,gross,net,status,fileName"));
        w.setLineAggregator(p -> String.join(",",
                String.valueOf(p.getId()),
                String.valueOf(p.getEmployeeId()),
                p.getMonth().toString(),
                String.format("%.2f", p.getGross()),
                String.format("%.2f", p.getNet()),
                safe(p.getStatus()),
                safe(p.getFileName())
        ));
        return w;
    }

    // --- CORRECTION DU TYPE ICI (JpaPagingItemReader au lieu de ItemReader) ---
    @Bean
    public Step exportPayslipsStep(JpaPagingItemReader<Payslip> payslipDbReader,
                                   FlatFileItemWriter<Payslip> payslipCsvWriter) {
        return new StepBuilder("exportPayslipsStep", jobRepository)
                .<Payslip, Payslip>chunk(50, tx)
                .reader(payslipDbReader)
                .processor((ItemProcessor<Payslip, Payslip>) item -> item) // passthrough
                .writer(payslipCsvWriter)
                .build();
    }

    @Bean
    public Job exportPayslipsJob(Step exportPayslipsStep) {
        return new JobBuilder("exportPayslipsJob", jobRepository)
                .start(exportPayslipsStep)
                .build();
    }

    // =========================================================
    // ===============   EXECUTION CONDITIONNELLE   ============
    // =========================================================

    @Bean
    public CommandLineRunner runBatch(
            PayslipRepository payslipRepo,
            JobLauncher jobLauncher,
            Job generatePayslipsJob
    ) {
        return args -> {
            if (payslipRepo.count() == 0) {
                System.out.println("⚠️ Base vide : Lancement automatique du Batch initial...");
                org.springframework.batch.core.JobParameters params = new org.springframework.batch.core.JobParametersBuilder()
                        .addLong("startedAt", System.currentTimeMillis())
                        .toJobParameters();
                jobLauncher.run(generatePayslipsJob, params);
            } else {
                System.out.println("✅ Batch ignoré (données existantes). Utilisez le bouton 'Générer' de l'app.");
            }
        };
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.replace(",", " ");
    }
}