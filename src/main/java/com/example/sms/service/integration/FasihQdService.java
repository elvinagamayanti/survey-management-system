package com.example.sms.service.integration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.sms.dto.integration.FasihQdDataDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.integration.IntegrationTracking;

public interface FasihQdService {
    
    /**
     * Get questionnaire design data for a specific kegiatan from FASIH-QD
     * @param kegiatanId the SMS kegiatan ID
     * @return Map containing design data from FASIH-QD
     */
    Map<String, Object> getKegiatanDesignData(Long kegiatanId);
    
    /**
     * Get detailed questionnaire design data for a specific kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return FasihQdDataDto containing structured design data
     */
    FasihQdDataDto getDetailedDesignData(Long kegiatanId);
    
    /**
     * Get all questionnaires associated with a kegiatan in FASIH-QD
     * @param kegiatanId the SMS kegiatan ID
     * @return List of questionnaire identifiers
     */
    List<String> getKegiatanQuestionnaires(Long kegiatanId);
    
    /**
     * Update questionnaire design status in FASIH-QD
     * @param kegiatanId the SMS kegiatan ID
     * @param status the new status to set
     * @return true if update was successful
     */
    boolean updateQuestionnaireStatus(Long kegiatanId, String status);
    
    /**
     * Create a new questionnaire design record in FASIH-QD
     * @param kegiatan the SMS kegiatan entity
     * @param designData initial design data
     * @return the external FASIH-QD ID for the created record
     */
    String createQuestionnaireDesign(Kegiatan kegiatan, Map<String, Object> designData);
    
    /**
     * Sync data for a specific kegiatan with FASIH-QD
     * @param kegiatanId the SMS kegiatan ID
     * @return IntegrationTracking record with the sync result
     */
    IntegrationTracking syncKegiatanWithFasihQd(Long kegiatanId);
    
    /**
     * Get questionnaire templates available in FASIH-QD
     * @return List of available questionnaire templates
     */
    List<Map<String, Object>> getQuestionnaireTemplates();
    
    /**
     * Get design progress metrics for a kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return Map containing design progress metrics
     */
    Map<String, Object> getDesignProgressMetrics(Long kegiatanId);
    
    /**
     * Find the external FASIH-QD ID for a kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return Optional containing the external FASIH-QD ID, or empty if not found
     */
    Optional<String> findExternalFasihQdId(Long kegiatanId);
}