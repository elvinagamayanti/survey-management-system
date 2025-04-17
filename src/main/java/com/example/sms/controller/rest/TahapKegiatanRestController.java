package com.example.sms.controller.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sms.dto.SubStepDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import com.example.sms.entity.TahapKegiatan;
import com.example.sms.service.KegiatanService;
import com.example.sms.service.TahapKegiatanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1")
public class TahapKegiatanRestController {
    private final TahapKegiatanService tahapKegiatanService;
    private final KegiatanService kegiatanService;
    
    @Autowired
    public TahapKegiatanRestController(TahapKegiatanService tahapKegiatanService,
                                     KegiatanService kegiatanService) {
        this.tahapKegiatanService = tahapKegiatanService;
        this.kegiatanService = kegiatanService;
    }
    
    @GetMapping("/kegiatan/{kegiatanId}/tahapan")
    public ResponseEntity<Map<String, Object>> getAllTahapan(@PathVariable Long kegiatanId) {
        try {
            Kegiatan kegiatan = kegiatanService.findKegiatanById(kegiatanId);
            List<TahapKegiatan> tahapan = tahapKegiatanService.getTahapanByKegiatan(kegiatan);
            int overallProgress = tahapKegiatanService.calculateOverallProgress(kegiatan);
            
            // Format response
            Map<String, Object> response = new HashMap<>();
            response.put("kegiatanId", kegiatanId);
            response.put("kegiatanName", kegiatan.getName());
            response.put("overallProgress", overallProgress);
            response.put("tahapan", formatTahapanList(tahapan));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/kegiatan/{kegiatanId}/tahapan/{tahapId}/complete")
    public ResponseEntity<Map<String, Object>> completeTahap(
            @PathVariable Long kegiatanId,
            @PathVariable Long tahapId) {
        try {
            TahapKegiatan completedTahap = tahapKegiatanService.completeStepAndAdvance(tahapId);
            
            // Update appropriate external system based on tahap type
            updateExternalSystem(kegiatanId, completedTahap.getTahap());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tahap successfully completed",
                "tahap", completedTahap.getTahap().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    private void updateExternalSystem(Long kegiatanId, Tahap tahap) {
        // Update appropriate external system based on tahap type
        switch (tahap) {
            case SPECIFY_NEEDS:
                // Update iPlan
                break;
            case DESIGN:
            case BUILD:
                // Update FASIH-QD
                break;
            case COLLECT:
            case PROCESS:
            case ANALYZE:
            case DISSEMINATE:
            case EVALUATE:
                // Update FASIH-SM
                break;
        }
    }
    
    private List<Map<String, Object>> formatTahapanList(List<TahapKegiatan> tahapan) {
        // Format tahapan list for API response
        return tahapan.stream()
                .map(this::formatTahap)
                .collect(Collectors.toList());
    }
    
    private Map<String, Object> formatTahap(TahapKegiatan tahap) {
        Map<String, Object> formattedTahap = new HashMap<>();
        formattedTahap.put("id", tahap.getId());
        formattedTahap.put("tahap", tahap.getTahap().toString());
        formattedTahap.put("isCompleted", tahap.getIsCompleted());
        formattedTahap.put("isActive", tahap.getIsActive());
        formattedTahap.put("progressPercentage", tahap.getProgressPercentage());
        
        // Parse sub-steps from JSON string
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<SubStepDto> subSteps = objectMapper.readValue(
                tahap.getSubSteps(), 
                new TypeReference<List<SubStepDto>>() {}
            );
            formattedTahap.put("subSteps", subSteps);
        } catch (Exception e) {
            formattedTahap.put("subSteps", Collections.emptyList());
        }
        
        return formattedTahap;
    }
}