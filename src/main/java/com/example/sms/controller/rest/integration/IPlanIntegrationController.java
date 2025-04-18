package com.example.sms.controller.rest.integration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sms.service.integration.IPlanService;

@RestController
@RequestMapping("/api/v1/integration/iplan")
public class IPlanIntegrationController {
    private final IPlanService iPlanService;
    
    @Autowired
    public IPlanIntegrationController(IPlanService iPlanService) {
        this.iPlanService = iPlanService;
    }
    
    @GetMapping("/kegiatan/{kegiatanId}")
    public ResponseEntity<Map<String, Object>> getIPlanData(@PathVariable Long kegiatanId) {
        Map<String, Object> data = iPlanService.getKegiatanPlanningData(kegiatanId);
        return ResponseEntity.ok(data);
    }
    
    // Other endpoints for iPlan integration
}
