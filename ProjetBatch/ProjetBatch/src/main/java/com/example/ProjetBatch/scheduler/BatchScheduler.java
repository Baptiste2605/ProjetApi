package com.example.ProjetBatch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job job;

    // Attention au nom du bean "generatePayslipsJob", vérifie dans BatchConfiguration.java s'il s'appelle bien comme ça
    public BatchScheduler(JobLauncher jobLauncher, @Qualifier("generatePayslipsJob") Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    // Se lance toutes les 30 secondes (30000 millisecondes)
    @Scheduled(fixedRate = 30000)
    public void runJob() {
        try {
            System.out.println("⏰ Le Batch se réveille...");
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) 
                    .toJobParameters();
            
            jobLauncher.run(job, params);
            System.out.println("✅ Batch terminé.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}