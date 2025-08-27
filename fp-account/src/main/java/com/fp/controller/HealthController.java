package com.fp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.SystemHealth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    
    @Autowired
    private HealthEndpoint healthEndpoint;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        SystemHealth actuatorHealth = (SystemHealth) healthEndpoint.health();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", actuatorHealth.getStatus().getCode());
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "fp-account");
        
        // 包含详细的健康检查信息
        if (actuatorHealth.getComponents() != null && !actuatorHealth.getComponents().isEmpty()) {
            response.put("components", actuatorHealth.getComponents());
        }
        
        return ResponseEntity.ok(response);
    }
}