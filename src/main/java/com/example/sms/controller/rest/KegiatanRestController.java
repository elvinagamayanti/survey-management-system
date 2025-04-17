package com.example.sms.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sms.dto.KegiatanDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.service.KegiatanService;
import com.example.sms.service.integration.FasihQdService;
import com.example.sms.service.integration.FasihSmService;
import com.example.sms.service.integration.IPlanService;

@RestController
@RequestMapping("/api/v1")
public class KegiatanRestController {
    private final KegiatanService kegiatanService;
    private final IPlanService iPlanService;
    private final FasihQdService fasihQdService;
    private final FasihSmService fasihSmService;
    
    @Autowired
    public KegiatanRestController(KegiatanService kegiatanService, 
                                IPlanService iPlanService,
                                FasihQdService fasihQdService, 
                                FasihSmService fasihSmService) {
        this.kegiatanService = kegiatanService;
        this.iPlanService = iPlanService;
        this.fasihQdService = fasihQdService;
        this.fasihSmService = fasihSmService;
    }
    
    @GetMapping("/kegiatan")
    public ResponseEntity<List<KegiatanDto>> getAllKegiatan() {
        List<KegiatanDto> kegiatanList = kegiatanService.ambilDaftarKegiatan();
        return ResponseEntity.ok(kegiatanList);
    }
    
    @GetMapping("/kegiatan/{id}")
    public ResponseEntity<KegiatanDto> getKegiatanById(@PathVariable Long id) {
        KegiatanDto kegiatan = kegiatanService.cariKegiatanById(id);
        return ResponseEntity.ok(kegiatan);
    }
    
    @GetMapping("/kegiatan/{id}/progress")
    public ResponseEntity<Map<String, Object>> getKegiatanProgress(@PathVariable Long id) {
        Kegiatan kegiatan = kegiatanService.findKegiatanById(id);
        
        // Fetch progress data from external systems
        Map<String, Object> iPlanData = iPlanService.getKegiatanPlanningData(id);
        Map<String, Object> fasihQdData = fasihQdService.getKegiatanDesignData(id);
        Map<String, Object> fasihSmData = fasihSmService.getKegiatanCollectionData(id);
        
        // Combine data
        Map<String, Object> combinedProgress = new HashMap<>();
        combinedProgress.put("kegiatanId", id);
        combinedProgress.put("name", kegiatan.getName());
        combinedProgress.put("planning", iPlanData);
        combinedProgress.put("design", fasihQdData);
        combinedProgress.put("collection", fasihSmData);
        combinedProgress.put("overallProgress", calculateOverallProgress(iPlanData, fasihQdData, fasihSmData));
        
        return ResponseEntity.ok(combinedProgress);
    }
    
    // Additional endpoints
}