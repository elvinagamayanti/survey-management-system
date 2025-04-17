package com.example.sms.service.integration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.sms.dto.integration.FasihSmDataDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.integration.IntegrationTracking;

public interface FasihSmService {
    
    /**
     * Get survey collection data for a specific kegiatan from FASIH-SM
     * @param kegiatanId the SMS kegiatan ID
     * @return Map containing collection data from FASIH-SM
     */
    Map<String, Object> getKegiatanCollectionData(Long kegiatanId);
    
    /**
     * Get detailed survey management data for a specific kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return FasihSmDataDto containing structured survey data
     */
    FasihSmDataDto getDetailedSurveyData(Long kegiatanId);
    
    /**
     * Get collection statistics for a kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return Map containing collection statistics
     */
    Map<String, Object> getCollectionStatistics(Long kegiatanId);
    
    /**
     * Update survey status in FASIH-SM
     * @param kegiatanId the SMS kegiatan ID
     * @param status the new status to set
     * @return true if update was successful
     */
    boolean updateSurveyStatus(Long kegiatanId, String status);
    
    /**
     * Create a new survey record in FASIH-SM
     * @param kegiatan the SMS kegiatan entity
     * @param surveyData initial survey data
     * @return the external FASIH-SM ID for the created record
     */
    String createSurvey(Kegiatan kegiatan, Map<String, Object> surveyData);
    
    /**
     * Sync data for a specific kegiatan with FASIH-SM
     * @param kegiatanId the SMS kegiatan ID
     * @return IntegrationTracking record with the sync result
     */
    IntegrationTracking syncKegiatanWithFasihSm(Long kegiatanId);
    
    /**
     * Get enumerator performance data for a survey
     * @param kegiatanId the SMS kegiatan ID
     * @return List of enumerator performance metrics
     */
    List<Map<String, Object>> getEnumeratorPerformance(Long kegiatanId);
    
    /**
     * Get survey response data quality metrics
     * @param kegiatanId the SMS kegiatan ID
     * @return Map containing data quality metrics
     */
    Map<String, Object> getDataQualityMetrics(Long kegiatanId);
    
    /**
     * Get processed survey data ready for analysis
     * @param kegiatanId the SMS kegiatan ID
     * @return URL to download the processed data
     */
    String getProcessedDataUrl(Long kegiatanId);
    
    /**
     * Find the external FASIH-SM ID for a kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return Optional containing the external FASIH-SM ID, or empty if not found
     */
    Optional<String> findExternalFasihSmId(Long kegiatanId);
}