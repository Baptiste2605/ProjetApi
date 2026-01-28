package com.example.ProjectApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.ProjectApp.gateway")
public class ProjectAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectAppApplication.class, args);
	}
}