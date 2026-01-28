package com.example.ProjectApp.gateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "batch-service", url = "${app.batch.url}") 
public interface BatchGateway {
    @PostMapping("/batch/start")
    String triggerBatch();
}