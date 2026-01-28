package com.example.ProjetBatch.controller;
 
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/batch")
public class BatchLauncherController {
 
    private final JobLauncher jobLauncher;
    private final Job generatePayslipsJob;
 
    public BatchLauncherController(JobLauncher jobLauncher,
                                   @Qualifier("generatePayslipsJob") Job generatePayslipsJob) {
        this.jobLauncher = jobLauncher;
        this.generatePayslipsJob = generatePayslipsJob;
    }
 
    // Cette méthode sera appelée par ProjectApp
    @PostMapping("/start")
    public String startBatch() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // Paramètre unique pour forcer le relancement
                    .toJobParameters();
 
            jobLauncher.run(generatePayslipsJob, params);
            return "BATCH_STARTED";
        } catch (Exception e) {
            e.printStackTrace();
            return "BATCH_ERROR";
        }
    }
}
 