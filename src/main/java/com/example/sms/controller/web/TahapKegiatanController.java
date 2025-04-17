package com.example.sms.controller.web;

import com.example.sms.dto.SubStepDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import com.example.sms.entity.TahapKegiatan;
import com.example.sms.service.KegiatanService;
import com.example.sms.service.TahapKegiatanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class TahapKegiatanController {

    @Autowired
    private TahapKegiatanService tahapKegiatanService;
    
    @Autowired
    private KegiatanService kegiatanService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Update sub-steps (checklist)
    @PostMapping("/operator/surveys/{kegiatanId}/tahap/{tahapId}/substeps")
    @ResponseBody
    public ResponseEntity<?> updateSubSteps(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("tahapId") Long tahapId,
            @RequestBody List<SubStepDto> subSteps) {
        
        try {
            TahapKegiatan updatedTahap = tahapKegiatanService.updateSubSteps(tahapId, subSteps);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "progress", updatedTahap.getProgressPercentage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    // Mark step as completed
    @PostMapping("/operator/surveys/{kegiatanId}/tahap/{tahapId}/complete")
    @ResponseBody
    public ResponseEntity<?> completeStep(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("tahapId") Long tahapId) {
        
        try {
            TahapKegiatan completedTahap = tahapKegiatanService.completeStepAndAdvance(tahapId);
            
            // Get the next active step if any
            Kegiatan kegiatan = kegiatanService.findKegiatanById(kegiatanId);
            Optional<TahapKegiatan> nextActiveStep = tahapKegiatanService.getActiveStepByKegiatan(kegiatan);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "completedTahap", completedTahap.getTahap().toString(),
                "nextActiveTahap", nextActiveStep.isPresent() ? nextActiveStep.get().getTahap().toString() : null
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    // Upload file for a step
    @PostMapping("/operator/surveys/{kegiatanId}/tahap/{tahapId}/upload")
    @ResponseBody
    public ResponseEntity<?> uploadFile(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("tahapId") Long tahapId,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "File kosong"
            ));
        }
        
        try {
            // Create a unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // Create the upload directory if it doesn't exist
            String uploadDir = "uploads/kegiatan/" + kegiatanId + "/tahap/" + tahapId;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Save the file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // Record the file upload in the database
            TahapKegiatan tahapKegiatan = tahapKegiatanService.recordFileUpload(tahapId, uniqueFilename);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "filename", uniqueFilename,
                "originalFilename", originalFilename
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Gagal mengupload file: " + e.getMessage()
            ));
        }
    }
    
    // Get step details
    @GetMapping("/operator/surveys/{kegiatanId}/tahap/{tahapNama}")
    @ResponseBody
    public ResponseEntity<?> getStepDetails(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("tahapNama") String tahapNama) {
        
        try {
            Kegiatan kegiatan = kegiatanService.findKegiatanById(kegiatanId);
            Tahap tahap = Tahap.valueOf(tahapNama.toUpperCase());
            
            Optional<TahapKegiatan> tahapKegiatan = tahapKegiatanService.getTahapByKegiatanAndTahap(kegiatan, tahap);
            
            if (tahapKegiatan.isPresent()) {
                TahapKegiatan step = tahapKegiatan.get();
                List<SubStepDto> subSteps = objectMapper.readValue(
                    step.getSubSteps(), 
                    new TypeReference<List<SubStepDto>>() {}
                );
                
                return ResponseEntity.ok(Map.of(
                    "id", step.getId(),
                    "tahap", step.getTahap().toString(),
                    "isCompleted", step.getIsCompleted(),
                    "isActive", step.getIsActive(),
                    "progressPercentage", step.getProgressPercentage(),
                    "subSteps", subSteps,
                    "uploadedFiles", step.getUploadedFiles() != null ? step.getUploadedFiles().split(",") : new String[0]
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Tahap tidak valid: " + tahapNama
            ));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error parsing sub-steps: " + e.getMessage()
            ));
        }
    }
    
    // Get all steps for a kegiatan
    @GetMapping("/operator/surveys/{kegiatanId}/tahapan")
    @ResponseBody
    public ResponseEntity<?> getAllSteps(@PathVariable("kegiatanId") Long kegiatanId) {
        try {
            Kegiatan kegiatan = kegiatanService.findKegiatanById(kegiatanId);
            
            // Initialize steps if they don't exist
            tahapKegiatanService.initializeStepsForKegiatan(kegiatan);
            
            List<TahapKegiatan> steps = tahapKegiatanService.getTahapanByKegiatan(kegiatan);
            int overallProgress = tahapKegiatanService.calculateOverallProgress(kegiatan);
            
            // Transform steps into a more useful format for the frontend
            List<Map<String, Object>> stepsData = steps.stream()
                .map(step -> {
                    try {
                        List<SubStepDto> subSteps = objectMapper.readValue(
                            step.getSubSteps(), 
                            new TypeReference<List<SubStepDto>>() {}
                        );
                        
                        return Map.of(
                            "id", step.getId(),
                            "tahap", step.getTahap().toString(),
                            "isCompleted", step.getIsCompleted(),
                            "isActive", step.getIsActive(),
                            "progressPercentage", step.getProgressPercentage(),
                            "subSteps", subSteps
                        );
                    } catch (JsonProcessingException e) {
                        return Map.of(
                            "id", step.getId(),
                            "tahap", step.getTahap().toString(),
                            "isCompleted", step.getIsCompleted(),
                            "isActive", step.getIsActive(),
                            "progressPercentage", step.getProgressPercentage(),
                            "subSteps", List.of()
                        );
                    }
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "steps", stepsData,
                "overallProgress", overallProgress
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}