package com.example.ProjetBatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; 

@SpringBootApplication
@EnableScheduling 
public class ProjetBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjetBatchApplication.class, args);
    }
}