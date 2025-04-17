package com.example.sms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sms.dto.SubStepDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import com.example.sms.entity.TahapKegiatan;
import com.example.sms.repository.TahapKegiatanRepository;
import com.example.sms.service.TahapKegiatanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TahapKegiatanServiceImpl implements TahapKegiatanService {

    @Autowired
    private TahapKegiatanRepository tahapKegiatanRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public void initializeStepsForKegiatan(Kegiatan kegiatan) {
        // Check if steps already exist
        List<TahapKegiatan> existingSteps = tahapKegiatanRepository.findByKegiatanOrderByTahap(kegiatan);
        if (!existingSteps.isEmpty()) {
            return; // Steps already initialized
        }
        
        // Create default sub-steps for each tahap
        Map<Tahap, List<SubStepDto>> defaultSubSteps = createDefaultSubSteps();
        
        // Create steps in order
        int stepOrder = 0;
        for (Tahap tahap : Tahap.values()) {
            try {
                TahapKegiatan tahapKegiatan = new TahapKegiatan();
                tahapKegiatan.setKegiatan(kegiatan);
                tahapKegiatan.setTahap(tahap);
                tahapKegiatan.setIsCompleted(false);
                
                // First step is active by default
                tahapKegiatan.setIsActive(stepOrder == 0);
                tahapKegiatan.setProgressPercentage(0);
                
                // Set default sub-steps
                String subStepsJson = objectMapper.writeValueAsString(defaultSubSteps.get(tahap));
                tahapKegiatan.setSubSteps(subStepsJson);
                
                tahapKegiatanRepository.save(tahapKegiatan);
                stepOrder++;
                
                System.out.println("Created step: " + tahap + " for kegiatan: " + kegiatan.getId());
            } catch (Exception e) {
                System.err.println("Error creating step " + tahap + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Map<Tahap, List<SubStepDto>> createDefaultSubSteps() {
        Map<Tahap, List<SubStepDto>> defaultSteps = new HashMap<>();
        
        // SPECIFY_NEEDS default sub-steps
        List<SubStepDto> specifyNeedsSteps = new ArrayList<>();
        specifyNeedsSteps.add(new SubStepDto("Identifikasi kebutuhan data", false));
        specifyNeedsSteps.add(new SubStepDto("Perumusan strategi survei", false));
        specifyNeedsSteps.add(new SubStepDto("Penentuan konten kuesioner", false));
        specifyNeedsSteps.add(new SubStepDto("Analisis kebutuhan pengguna", false));
        specifyNeedsSteps.add(new SubStepDto("Rencana anggaran kegiatan", false));
        specifyNeedsSteps.add(new SubStepDto("Penetapan tim pelaksana survei", false));
        defaultSteps.put(Tahap.SPECIFY_NEEDS, specifyNeedsSteps);
        
        // DESIGN default sub-steps
        List<SubStepDto> designSteps = new ArrayList<>();
        designSteps.add(new SubStepDto("Penyusunan kuesioner", false));
        designSteps.add(new SubStepDto("Perancangan metodologi survei", false));
        designSteps.add(new SubStepDto("Penentuan kerangka sampel", false));
        designSteps.add(new SubStepDto("Pemilihan teknik pengumpulan data", false));
        defaultSteps.put(Tahap.DESIGN, designSteps);
        
        // BUILD default sub-steps
        List<SubStepDto> buildSteps = new ArrayList<>();
        buildSteps.add(new SubStepDto("Pembuatan aplikasi survei", false));
        buildSteps.add(new SubStepDto("Penyiapan instrumen pengumpulan data", false));
        buildSteps.add(new SubStepDto("Pengujian instrumen", false));
        buildSteps.add(new SubStepDto("Finalisasi kuesioner", false));
        defaultSteps.put(Tahap.BUILD, buildSteps);
        
        // COLLECT default sub-steps
        List<SubStepDto> collectSteps = new ArrayList<>();
        collectSteps.add(new SubStepDto("Pelaksanaan pengumpulan data", false));
        collectSteps.add(new SubStepDto("Monitoring pengumpulan data", false));
        collectSteps.add(new SubStepDto("Validasi data", false));
        collectSteps.add(new SubStepDto("Kompilasi data mentah", false));
        defaultSteps.put(Tahap.COLLECT, collectSteps);
        
        // PROCESS default sub-steps
        List<SubStepDto> processSteps = new ArrayList<>();
        processSteps.add(new SubStepDto("Pembersihan data", false));
        processSteps.add(new SubStepDto("Pemrosesan data mentah", false));
        processSteps.add(new SubStepDto("Konversi format data", false));
        processSteps.add(new SubStepDto("Imputasi data", false));
        defaultSteps.put(Tahap.PROCESS, processSteps);
        
        // ANALYZE default sub-steps
        List<SubStepDto> analyzeSteps = new ArrayList<>();
        analyzeSteps.add(new SubStepDto("Analisis statistik", false));
        analyzeSteps.add(new SubStepDto("Interpretasi hasil", false));
        analyzeSteps.add(new SubStepDto("Pembuatan tabel dan grafik", false));
        analyzeSteps.add(new SubStepDto("Penulisan temuan utama", false));
        defaultSteps.put(Tahap.ANALYZE, analyzeSteps);
        
        // DISSEMINATE default sub-steps
        List<SubStepDto> disseminateSteps = new ArrayList<>();
        disseminateSteps.add(new SubStepDto("Penulisan laporan", false));
        disseminateSteps.add(new SubStepDto("Pembuatan publikasi", false));
        disseminateSteps.add(new SubStepDto("Pembuatan infografis", false));
        disseminateSteps.add(new SubStepDto("Penyebaran hasil survei", false));
        defaultSteps.put(Tahap.DISSEMINATE, disseminateSteps);
        
        // EVALUATE default sub-steps
        List<SubStepDto> evaluateSteps = new ArrayList<>();
        evaluateSteps.add(new SubStepDto("Evaluasi proses survei", false));
        evaluateSteps.add(new SubStepDto("Identifikasi kendala", false));
        evaluateSteps.add(new SubStepDto("Penulisan rekomendasi", false));
        evaluateSteps.add(new SubStepDto("Dokumentasi pembelajaran", false));
        defaultSteps.put(Tahap.EVALUATE, evaluateSteps);
        
        return defaultSteps;
    }

    @Override
    public List<TahapKegiatan> getTahapanByKegiatan(Kegiatan kegiatan) {
        return tahapKegiatanRepository.findByKegiatanOrderByTahap(kegiatan);
    }

    @Override
    public Optional<TahapKegiatan> getTahapByKegiatanAndTahap(Kegiatan kegiatan, Tahap tahap) {
        return tahapKegiatanRepository.findByKegiatanAndTahap(kegiatan, tahap);
    }

    @Override
    public Optional<TahapKegiatan> getActiveStepByKegiatan(Kegiatan kegiatan) {
        return tahapKegiatanRepository.findActiveStepByKegiatanId(kegiatan.getId());
    }

    @Override
    @Transactional
    public TahapKegiatan updateStepProgress(Long tahapKegiatanId, Integer progressPercentage) {
        TahapKegiatan tahapKegiatan = tahapKegiatanRepository.findById(tahapKegiatanId)
                .orElseThrow(() -> new RuntimeException("Tahap kegiatan tidak ditemukan"));
        
        tahapKegiatan.setProgressPercentage(progressPercentage);
        return tahapKegiatanRepository.save(tahapKegiatan);
    }

    @Override
    @Transactional
    public TahapKegiatan completeStepAndAdvance(Long tahapKegiatanId) {
        TahapKegiatan currentStep = tahapKegiatanRepository.findById(tahapKegiatanId)
                .orElseThrow(() -> new RuntimeException("Tahap kegiatan tidak ditemukan"));
        
        // Mark current step as completed
        currentStep.setIsCompleted(true);
        currentStep.setProgressPercentage(100);
        currentStep.setIsActive(false);
        tahapKegiatanRepository.save(currentStep);
        
        // Find the next step in sequence
        Tahap[] tahapValues = Tahap.values();
        int currentOrdinal = currentStep.getTahap().ordinal();
        
        if (currentOrdinal < tahapValues.length - 1) {
            Tahap nextTahap = tahapValues[currentOrdinal + 1];
            TahapKegiatan nextStep = tahapKegiatanRepository
                    .findByKegiatanAndTahap(currentStep.getKegiatan(), nextTahap)
                    .orElseThrow(() -> new RuntimeException("Next step not found"));
            
            nextStep.setIsActive(true);
            tahapKegiatanRepository.save(nextStep);
        }
        
        return currentStep;
    }

    @Override
    @Transactional
    public TahapKegiatan updateSubSteps(Long tahapKegiatanId, List<SubStepDto> subSteps) {
        TahapKegiatan tahapKegiatan = tahapKegiatanRepository.findById(tahapKegiatanId)
                .orElseThrow(() -> new RuntimeException("Tahap kegiatan tidak ditemukan"));
        
        try {
            tahapKegiatan.setSubSteps(objectMapper.writeValueAsString(subSteps));
            
            // Calculate progress based on checked sub-steps
            int totalSteps = subSteps.size();
            if (totalSteps > 0) {
                long completedSteps = subSteps.stream().filter(SubStepDto::isCompleted).count();
                int progressPercentage = (int) ((completedSteps * 100) / totalSteps);
                tahapKegiatan.setProgressPercentage(progressPercentage);
            }
            
            return tahapKegiatanRepository.save(tahapKegiatan);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error updating sub-steps", e);
        }
    }

    @Override
    public int calculateOverallProgress(Kegiatan kegiatan) {
        List<TahapKegiatan> steps = tahapKegiatanRepository.findByKegiatanOrderByTahap(kegiatan);
        
        if (steps.isEmpty()) {
            return 0;
        }
        
        int totalSteps = steps.size();
        int completedSteps = (int) steps.stream().filter(TahapKegiatan::getIsCompleted).count();
        int activeStepProgress = 0;
        
        // Get progress of active step
        Optional<TahapKegiatan> activeStep = steps.stream()
                .filter(TahapKegiatan::getIsActive)
                .findFirst();
        
        if (activeStep.isPresent()) {
            activeStepProgress = activeStep.get().getProgressPercentage();
        }
        
        // Overall progress = (completed steps / total steps) * 100 + (active step progress / 100) * (100 / total steps)
        return (completedSteps * 100) / totalSteps + (activeStepProgress * (100 / totalSteps)) / 100;
    }

    @Override
    @Transactional
    public TahapKegiatan recordFileUpload(Long tahapKegiatanId, String fileName) {
        TahapKegiatan tahapKegiatan = tahapKegiatanRepository.findById(tahapKegiatanId)
                .orElseThrow(() -> new RuntimeException("Tahap kegiatan tidak ditemukan"));
        
        String uploadedFiles = tahapKegiatan.getUploadedFiles();
        if (uploadedFiles == null || uploadedFiles.isEmpty()) {
            tahapKegiatan.setUploadedFiles(fileName);
        } else {
            tahapKegiatan.setUploadedFiles(uploadedFiles + "," + fileName);
        }
        
        return tahapKegiatanRepository.save(tahapKegiatan);
    }

    @Override
    public Map<Tahap, Boolean> getStepStatusesByKegiatan(Kegiatan kegiatan) {
        List<TahapKegiatan> steps = tahapKegiatanRepository.findByKegiatanOrderByTahap(kegiatan);
        
        Map<Tahap, Boolean> stepStatuses = new HashMap<>();
        for (Tahap tahap : Tahap.values()) {
            stepStatuses.put(tahap, false); // Default all steps to false (not completed)
        }
        
        // Mark completed steps
        steps.stream()
            .filter(TahapKegiatan::getIsCompleted)
            .forEach(step -> stepStatuses.put(step.getTahap(), true));
        
        return stepStatuses;
    }
}
