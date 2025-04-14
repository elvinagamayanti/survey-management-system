package com.example.sms.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.sms.dto.SubStepDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import com.example.sms.entity.TahapKegiatan;

public interface TahapKegiatanService {
    
    // Initialize steps for a new kegiatan
    void initializeStepsForKegiatan(Kegiatan kegiatan);
    
    // Get all steps for a kegiatan
    List<TahapKegiatan> getTahapanByKegiatan(Kegiatan kegiatan);
    
    // Get specific step
    Optional<TahapKegiatan> getTahapByKegiatanAndTahap(Kegiatan kegiatan, Tahap tahap);
    
    // Get current active step
    Optional<TahapKegiatan> getActiveStepByKegiatan(Kegiatan kegiatan);
    
    // Update step progress
    TahapKegiatan updateStepProgress(Long tahapKegiatanId, Integer progressPercentage);
    
    // Mark step as completed and activate next step
    TahapKegiatan completeStepAndAdvance(Long tahapKegiatanId);
    
    // Update sub-steps (checklist items)
    TahapKegiatan updateSubSteps(Long tahapKegiatanId, List<SubStepDto> subSteps);
    
    // Calculate overall kegiatan progress
    int calculateOverallProgress(Kegiatan kegiatan);
    
    // Record file upload for a step
    TahapKegiatan recordFileUpload(Long tahapKegiatanId, String fileName);
    
    // Get all step statuses as a map
    Map<Tahap, Boolean> getStepStatusesByKegiatan(Kegiatan kegiatan);
}