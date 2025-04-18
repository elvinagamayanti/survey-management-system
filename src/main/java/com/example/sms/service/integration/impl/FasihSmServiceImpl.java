package com.example.sms.service.integration.impl;

import com.example.sms.dto.integration.FasihSmDataDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.integration.ExternalSystemMapping;
import com.example.sms.entity.integration.IntegrationTracking;
import com.example.sms.repository.KegiatanRepository;
import com.example.sms.repository.integration.ExternalSystemMappingRepository;
import com.example.sms.repository.integration.IntegrationTrackingRepository;
import com.example.sms.service.database.FasihSmDatabaseService;
import com.example.sms.service.integration.FasihSmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FasihSmServiceImpl implements FasihSmService {
    
    private static final Logger logger = LoggerFactory.getLogger(FasihSmServiceImpl.class);
    private static final String EXTERNAL_SYSTEM = "FASIH_SM";
    
    private final RestTemplate restTemplate;
    private final String fasihSmBaseUrl;
    private final String apiKey;
    private final KegiatanRepository kegiatanRepository;
    private final ExternalSystemMappingRepository mappingRepository;
    private final IntegrationTrackingRepository trackingRepository;
    private final FasihSmDatabaseService fasihSmDatabaseService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public FasihSmServiceImpl(
            RestTemplate restTemplate,
            @Value("${fasih-sm.api.base-url}") String fasihSmBaseUrl,
            @Value("${fasih-sm.api.key}") String apiKey,
            KegiatanRepository kegiatanRepository,
            ExternalSystemMappingRepository mappingRepository,
            IntegrationTrackingRepository trackingRepository,
            FasihSmDatabaseService fasihSmDatabaseService,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.fasihSmBaseUrl = fasihSmBaseUrl;
        this.apiKey = apiKey;
        this.kegiatanRepository = kegiatanRepository;
        this.mappingRepository = mappingRepository;
        this.trackingRepository = trackingRepository;
        this.fasihSmDatabaseService = fasihSmDatabaseService;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Map<String, Object> getKegiatanCollectionData(Long kegiatanId) {
        logger.info("Fetching survey collection data for kegiatan ID: {}", kegiatanId);
        
        Optional<String> externalIdOpt = findExternalFasihSmId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external FASIH-SM ID found for kegiatan ID: {}", kegiatanId);
            return createErrorResponse("No external FASIH-SM ID mapping found");
        }
        
        String externalId = externalIdOpt.get();
        String url = fasihSmBaseUrl + "/api/survey/" + externalId;
        
        try {
            ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), 
                                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK) {
                // Update tracking record
                updateTrackingRecord(kegiatanId, "SUCCESS", null, response.getBody());
                return response.getBody();
            }
            
            String errorMessage = "Error fetching data: " + response.getStatusCode();
            updateTrackingRecord(kegiatanId, "FAILED", errorMessage, null);
            return createErrorResponse(errorMessage);
        } catch (RestClientException e) {
            String errorMessage = "Error connecting to FASIH-SM: " + e.getMessage();
            logger.error(errorMessage, e);
            updateTrackingRecord(kegiatanId, "FAILED", errorMessage, null);
            return createErrorResponse(errorMessage);
        }
    }
    
    @Override
    public FasihSmDataDto getDetailedSurveyData(Long kegiatanId) {
        Map<String, Object> rawData = getKegiatanCollectionData(kegiatanId);
        
        if (rawData.containsKey("error") && (Boolean) rawData.get("error")) {
            logger.warn("Error in raw data for kegiatan ID: {}", kegiatanId);
            return null;
        }
        
        try {
            // Convert raw map data to FasihSmDataDto
            String json = objectMapper.writeValueAsString(rawData);
            return objectMapper.readValue(json, FasihSmDataDto.class);
        } catch (JsonProcessingException e) {
            logger.error("Error converting raw data to FasihSmDataDto: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public Map<String, Object> getCollectionStatistics(Long kegiatanId) {
        Optional<String> externalIdOpt = findExternalFasihSmId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external FASIH-SM ID found for kegiatan ID: {}", kegiatanId);
            return createErrorResponse("No external FASIH-SM ID mapping found");
        }
        
        String externalId = externalIdOpt.get();
        String url = fasihSmBaseUrl + "/api/survey/" + externalId + "/statistics";
        
        try {
            ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), 
                                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
            logger.warn("Error fetching collection statistics: {}", response.getStatusCode());
            return createErrorResponse("Error fetching statistics: " + response.getStatusCode());
        } catch (RestClientException e) {
            logger.error("Error connecting to FASIH-SM: {}", e.getMessage(), e);
            return createErrorResponse("Error connecting to FASIH-SM: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateSurveyStatus(Long kegiatanId, String status) {
        Optional<String> externalIdOpt = findExternalFasihSmId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external FASIH-SM ID found for kegiatan ID: {}", kegiatanId);
            return false;
        }
        
        String externalId = externalIdOpt.get();
        String url = fasihSmBaseUrl + "/api/survey/" + externalId + "/status";
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", status);
        
        try {
            ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(requestBody, getHeaders()), 
                                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK) {
                updateTrackingRecord(kegiatanId, "SUCCESS", null, null);
                return true;
            }
            
            String errorMessage = "Error updating status: " + response.getStatusCode();
            updateTrackingRecord(kegiatanId, "FAILED", errorMessage, null);
            return false;
        } catch (RestClientException e) {
            String errorMessage = "Error connecting to FASIH-SM: " + e.getMessage();
            logger.error(errorMessage, e);
            updateTrackingRecord(kegiatanId, "FAILED", errorMessage, null);
            return false;
        }
    }
    
    @Override
    @Transactional
    public String createSurvey(Kegiatan kegiatan, Map<String, Object> surveyData) {
        String url = fasihSmBaseUrl + "/api/survey";
        
        // Add kegiatan metadata to survey data
        surveyData.put("kegiatanCode", kegiatan.getCode());
        surveyData.put("kegiatanName", kegiatan.getName());
        surveyData.put("smsKegiatanId", kegiatan.getId());
        
        try {
            ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(surveyData, getHeaders()), 
                                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                String externalId = (String) response.getBody().get("id");
                
                // Create or update mapping
                createOrUpdateMapping(kegiatan, externalId);
                
                // Update tracking record
                IntegrationTracking tracking = trackingRepository
                    .findByKegiatanAndExternalSystem(kegiatan, EXTERNAL_SYSTEM)
                    .orElse(new IntegrationTracking());
                
                tracking.setKegiatan(kegiatan);
                tracking.setExternalSystem(EXTERNAL_SYSTEM);
                tracking.setExternalId(externalId);
                tracking.setLastSyncTime(LocalDateTime.now());
                tracking.setSyncStatus("SUCCESS");
                tracking.setSyncCount(tracking.getSyncCount() == null ? 1 : tracking.getSyncCount() + 1);
                tracking.setErrorMessage(null);
                
                // Save tracking record
                trackingRepository.save(tracking);
                
                return externalId;
            }
            
            String errorMessage = "Error creating survey: " + response.getStatusCode();
            logger.error(errorMessage);
            return null;
        } catch (RestClientException e) {
            String errorMessage = "Error connecting to FASIH-SM: " + e.getMessage();
            logger.error(errorMessage, e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public IntegrationTracking syncKegiatanWithFasihSm(Long kegiatanId) {
        Kegiatan kegiatan = kegiatanRepository.findById(kegiatanId)
            .orElseThrow(() -> new IllegalArgumentException("Kegiatan not found with ID: " + kegiatanId));
        
        IntegrationTracking tracking = trackingRepository
            .findByKegiatanAndExternalSystem(kegiatan, EXTERNAL_SYSTEM)
            .orElse(new IntegrationTracking());
        
        tracking.setKegiatan(kegiatan);
        tracking.setExternalSystem(EXTERNAL_SYSTEM);
        tracking.setLastSyncTime(LocalDateTime.now());
        tracking.setSyncCount(tracking.getSyncCount() == null ? 1 : tracking.getSyncCount() + 1);
        
        try {
            // If no external ID exists, try to create new survey in FASIH-SM
            if (tracking.getExternalId() == null) {
                Map<String, Object> surveyData = new HashMap<>();
                surveyData.put("name", kegiatan.getName());
                surveyData.put("code", kegiatan.getCode());
                // Add other relevant data
                
                String externalId = createSurvey(kegiatan, surveyData);
                if (externalId != null) {
                    tracking.setExternalId(externalId);
                    tracking.setSyncStatus("SUCCESS");
                    tracking.setErrorMessage(null);
                } else {
                    tracking.setSyncStatus("FAILED");
                    tracking.setErrorMessage("Failed to create survey in FASIH-SM");
                }
            } else {
                // Get survey data to check if it exists
                Map<String, Object> surveyData = getKegiatanCollectionData(kegiatanId);
                if (!surveyData.containsKey("error")) {
                    tracking.setSyncStatus("SUCCESS");
                    tracking.setErrorMessage(null);
                    
                    // Save hash of data for future comparison
                    try {
                        String dataHash = objectMapper.writeValueAsString(surveyData).hashCode() + "";
                        tracking.setDataHash(dataHash);
                    } catch (JsonProcessingException e) {
                        logger.warn("Failed to create hash for survey data", e);
                    }
                } else {
                    tracking.setSyncStatus("FAILED");
                    tracking.setErrorMessage((String) surveyData.get("message"));
                }
            }
        } catch (Exception e) {
            tracking.setSyncStatus("FAILED");
            tracking.setErrorMessage("Error synchronizing with FASIH-SM: " + e.getMessage());
            logger.error("Error synchronizing with FASIH-SM for kegiatan ID: {}", kegiatanId, e);
        }
        
        return trackingRepository.save(tracking);
    }
    
    @Override
    public List<Map<String, Object>> getEnumeratorPerformance(Long kegiatanId) {
        Optional<String> externalIdOpt = findExternalFasihSmId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external FASIH-SM ID found for kegiatan ID: {}", kegiatanId);
            return Collections.emptyList();
        }
        
        String externalId = externalIdOpt.get();
        String url = fasihSmBaseUrl + "/api/survey/" + externalId + "/enumerators/performance";
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = 
                restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), 
                                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
            logger.warn("Error fetching enumerator performance: {}", response.getStatusCode());
            return Collections.emptyList();
        } catch (RestClientException e) {
            logger.error("Error connecting to FASIH-SM: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> getDataQualityMetrics(Long kegiatanId) {
        Optional<String> externalIdOpt = findExternalFasihSmId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external FASIH-SM ID found for kegiatan ID: {}", kegiatanId);
            return createErrorResponse("No external FASIH-SM ID mapping found");
        }
        
        String externalId = externalIdOpt.get();
        String url = fasihSmBaseUrl + "/api/survey/" + externalId + "/quality";
        
        try {
            ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), 
                                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
            logger.warn("Error fetching data quality metrics: {}", response.getStatusCode());
            return createErrorResponse("Error fetching metrics: " + response.getStatusCode());
        } catch (RestClientException e) {
            logger.error("Error connecting to FASIH-SM: {}", e.getMessage(), e);
            return createErrorResponse("Error connecting to FASIH-SM: " + e.getMessage());
        }
    }
    
    @Override
    public String getProcessedDataUrl(Long kegiatanId) {
        Optional<String> externalIdOpt = findExternalFasihSmId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external FASIH-SM ID found for kegiatan ID: {}", kegiatanId);
            return null;
        }
        
        String externalId = externalIdOpt.get();
        String url = fasihSmBaseUrl + "/api/survey/" + externalId + "/data/processed-url";
        
        try {
            ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), 
                                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("url");
            }
            
            logger.warn("Error fetching processed data URL: {}", response.getStatusCode());
            return null;
        } catch (RestClientException e) {
            logger.error("Error connecting to FASIH-SM: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public Optional<String> findExternalFasihSmId(Long kegiatanId) {
        // First check mapping table
        List<ExternalSystemMapping> mappings = mappingRepository.findByKegiatanIdAndExternalSystem(kegiatanId, EXTERNAL_SYSTEM);
        
        if (!mappings.isEmpty() && mappings.get(0).getExternalId() != null) {
            return Optional.of(mappings.get(0).getExternalId());
        }
        
        // Check tracking table as fallback
        Optional<IntegrationTracking> tracking = trackingRepository.findByKegiatanIdAndExternalSystem(kegiatanId, EXTERNAL_SYSTEM);
        
        if (tracking.isPresent() && tracking.get().getExternalId() != null) {
            // Also update mapping for future use
            Kegiatan kegiatan = kegiatanRepository.findById(kegiatanId)
                .orElseThrow(() -> new IllegalArgumentException("Kegiatan not found with ID: " + kegiatanId));
            
            createOrUpdateMapping(kegiatan, tracking.get().getExternalId());
            
            return Optional.of(tracking.get().getExternalId());
        }
        
        return Optional.empty();
    }
    
    private HttpEntity<?> getHttpEntity() {
        return new HttpEntity<>(getHeaders());
    }
    
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", apiKey);
        return headers;
    }
    
    private void updateTrackingRecord(Long kegiatanId, String status, String errorMessage, Map<String, Object> data) {
        try {
            Kegiatan kegiatan = kegiatanRepository.findById(kegiatanId)
                .orElseThrow(() -> new IllegalArgumentException("Kegiatan not found with ID: " + kegiatanId));
            
            IntegrationTracking tracking = trackingRepository
                .findByKegiatanAndExternalSystem(kegiatan, EXTERNAL_SYSTEM)
                .orElse(new IntegrationTracking());
            
            tracking.setKegiatan(kegiatan);
            tracking.setExternalSystem(EXTERNAL_SYSTEM);
            tracking.setLastSyncTime(LocalDateTime.now());
            tracking.setSyncStatus(status);
            tracking.setErrorMessage(errorMessage);
            tracking.setSyncCount(tracking.getSyncCount() == null ? 1 : tracking.getSyncCount() + 1);
            
            // Calculate hash for data change detection
            if (data != null) {
                try {
                    String dataHash = objectMapper.writeValueAsString(data).hashCode() + "";
                    tracking.setDataHash(dataHash);
                } catch (JsonProcessingException e) {
                    logger.warn("Failed to create hash for data", e);
                }
            }
            
            trackingRepository.save(tracking);
        } catch (Exception e) {
            logger.error("Error updating tracking record for kegiatan ID: {}", kegiatanId, e);
        }
    }
    
    private void createOrUpdateMapping(Kegiatan kegiatan, String externalId) {
        List<ExternalSystemMapping> mappings = mappingRepository.findByKegiatanAndExternalSystem(kegiatan, EXTERNAL_SYSTEM);
        
        ExternalSystemMapping mapping;
        if (mappings.isEmpty()) {
            mapping = new ExternalSystemMapping();
            mapping.setKegiatan(kegiatan);
            mapping.setExternalSystem(EXTERNAL_SYSTEM);
        } else {
            mapping = mappings.get(0);
        }
        
        mapping.setExternalId(externalId);
        mappingRepository.save(mapping);
    }
    
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", errorMessage);
        error.put("timestamp", LocalDateTime.now().toString());
        return error;
    }
}