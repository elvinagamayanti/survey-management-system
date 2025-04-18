package com.example.sms.service.integration;

import com.example.sms.dto.integration.IPlanDataDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.integration.IntegrationTracking;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IPlanService {
    
    /**
     * Get planning data for a specific kegiatan from iPlan
     * @param kegiatanId the SMS kegiatan ID
     * @return Map containing planning data from iPlan
     */
    Map<String, Object> getKegiatanPlanningData(Long kegiatanId);
    
    /**
     * Get detailed planning data for a specific kegiatan from iPlan
     * @param kegiatanId the SMS kegiatan ID
     * @return IPlanDataDto containing structured planning data
     */
    IPlanDataDto getDetailedPlanningData(Long kegiatanId);
    
    /**
     * Update kegiatan planning status in iPlan
     * @param kegiatanId the SMS kegiatan ID
     * @param status the new status to set
     * @return true if update was successful
     */
    boolean updateKegiatanPlanningStatus(Long kegiatanId, String status);
    
    /**
     * Create a new kegiatan planning record in iPlan
     * @param kegiatan the SMS kegiatan entity
     * @param planData initial planning data
     * @return the external iPlan ID for the created record
     */
    String createKegiatanPlanning(Kegiatan kegiatan, Map<String, Object> planData);
    
    /**
     * Sync data for a specific kegiatan with iPlan
     * @param kegiatanId the SMS kegiatan ID
     * @return IntegrationTracking record with the sync result
     */
    IntegrationTracking syncKegiatanWithIPlan(Long kegiatanId);
    
    /**
     * Get all available planning documents from iPlan for a kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return List of document metadata
     */
    List<Map<String, Object>> getPlanningDocuments(Long kegiatanId);
    
    /**
     * Get planning data for multiple kegiatan in a specified year
     * @param year the year to query
     * @return List of IPlanDataDto objects
     */
    List<IPlanDataDto> getPlanningDataByYear(String year);
    
    /**
     * Find the external iPlan ID for a kegiatan
     * @param kegiatanId the SMS kegiatan ID
     * @return Optional containing the external iPlan ID, or empty if not found
     */
    Optional<String> findExternalIPlanId(Long kegiatanId);
}