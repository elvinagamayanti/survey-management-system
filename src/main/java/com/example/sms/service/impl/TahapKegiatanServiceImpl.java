package com.example.sms.service.impl;

import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import com.example.sms.entity.TahapKegiatan;
import com.example.sms.repository.TahapKegiatanRepository;
import com.example.sms.service.TahapKegiatanService;
import com.example.sms.service.integration.ExternalSystemIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TahapKegiatanServiceImpl implements TahapKegiatanService {

    @Autowired
    private TahapKegiatanRepository tahapKegiatanRepository;
    
    @Autowired
    private ExternalSystemIntegrationService integrationService;
    
    // ... existing methods
    
    @Override
    public Map<Tahap, Boolean> getStepStatusesByKegiatan(Kegiatan kegiatan) {
        // First check local data
        Map<Tahap, Boolean> localStatus = getLocalStepStatuses(kegiatan);
        
        // Then check with external systems and merge results
        Map<Tahap, Boolean> externalStatus = 
            integrationService.getSurveyProgressStatus(kegiatan.getId(), kegiatan.getCode());
        
        // Merge the statuses, preferring external system data
        externalStatus.forEach((tahap, status) -> {
            if (status) {
                localStatus.put(tahap, true);
            }
        });
        
        return localStatus;
    }
    
    private Map<Tahap, Boolean> getLocalStepStatuses(Kegiatan kegiatan) {
        List<TahapKegiatan> steps = tahapKegiatanRepository.findByKegiatanOrderByTahap(kegiatan);
        
        Map<Tahap, Boolean> stepStatuses = Arrays.stream(Tahap.values())
            .collect(Collectors.toMap(tahap -> tahap, tahap -> false));
        
        // Mark completed steps
        steps.stream()
            .filter(TahapKegiatan::getIsCompleted)
            .forEach(step -> stepStatuses.put(step.getTahap(), true));
        
        return stepStatuses;
    }
    
    @Override
    @Transactional
    public void syncWithExternalSystems(Long kegiatanId) {
        // Implementation to sync progress data from external systems
        // This could be called periodically or on-demand
    }
}