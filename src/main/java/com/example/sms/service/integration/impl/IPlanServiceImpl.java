package com.example.sms.service.integration.impl;

import com.example.sms.dto.integration.IPlanDataDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.integration.ExternalSystemMapping;
import com.example.sms.entity.integration.IntegrationTracking;
import com.example.sms.repository.KegiatanRepository;
import com.example.sms.repository.integration.ExternalSystemMappingRepository;
import com.example.sms.repository.integration.IntegrationTrackingRepository;
import com.example.sms.service.database.IPlanDatabaseService;
import com.example.sms.service.integration.IPlanService;
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
public class IPlanServiceImpl implements IPlanService {
    
    private static final Logger logger = LoggerFactory.getLogger(IPlanServiceImpl.class);
    private static final String EXTERNAL_SYSTEM = "IPLAN";
    
    private final RestTemplate restTemplate;
    private final String iPlanBaseUrl;
    private final String apiKey;
    private final KegiatanRepository kegiatanRepository;
    private final ExternalSystemMappingRepository mappingRepository;
    private final IntegrationTrackingRepository trackingRepository;
    private final IPlanDatabaseService iPlanDatabaseService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public IPlanServiceImpl(
            RestTemplate restTemplate,
            @Value("${iplan.api.base-url}") String iPlanBaseUrl,
            @Value("${iplan.api.key}") String apiKey,
            KegiatanRepository kegiatanRepository,
            ExternalSystemMappingRepository mappingRepository,
            IntegrationTrackingRepository trackingRepository,
            IPlanDatabaseService iPlanDatabaseService,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.iPlanBaseUrl = iPlanBaseUrl;
        this.apiKey = apiKey;
        this.kegiatanRepository = kegiatanRepository;
        this.mappingRepository = mappingRepository;
        this.trackingRepository = trackingRepository;
        this.iPlanDatabaseService = iPlanDatabaseService;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Map<String, Object> getKegiatanPlanningData(Long kegiatanId) {
        logger.info("Fetching planning data for kegiatan ID: {}", kegiatanId);
        
        Optional<String> externalIdOpt = findExternalIPlanId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external iPlan ID found for kegiatan ID: {}", kegiatanId);
            return createErrorResponse("No external iPlan ID mapping found");
        }
        
        String externalId = externalIdOpt.get();
        String url = iPlanBaseUrl + "/api/planning/" + externalId;
        
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
            String errorMessage = "Error connecting to iPlan: " + e.getMessage();
            logger.error(errorMessage, e);
            updateTrackingRecord(kegiatanId, "FAILED", errorMessage, null);
            return createErrorResponse(errorMessage);
        }
    }
    
    @Override
    public IPlanDataDto getDetailedPlanningData(Long kegiatanId) {
        Map<String, Object> rawData = getKegiatanPlanningData(kegiatanId);
        
        if (rawData.containsKey("error") && (Boolean) rawData.get("error")) {
            logger.warn("Error in raw data for kegiatan ID: {}", kegiatanId);
            return null;
        }
        
        try {
            // Convert raw map data to IPlanDataDto
            String json = objectMapper.writeValueAsString(rawData);
            return objectMapper.readValue(json, IPlanDataDto.class);
        } catch (JsonProcessingException e) {
            logger.error("Error converting raw data to IPlanDataDto: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    @Transactional
    public boolean updateKegiatanPlanningStatus(Long kegiatanId, String status) {
        Optional<String> externalIdOpt = findExternalIPlanId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external iPlan ID found for kegiatan ID: {}", kegiatanId);
            return false;
        }
        
        String externalId = externalIdOpt.get();
        String url = iPlanBaseUrl + "/api/planning/" + externalId + "/status";
        
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
            String errorMessage = "Error connecting to iPlan: " + e.getMessage();
            logger.error(errorMessage, e);
            updateTrackingRecord(kegiatanId, "FAILED", errorMessage, null);
            return false;
        }
    }
    
    @Override
    @Transactional
    public String createKegiatanPlanning(Kegiatan kegiatan, Map<String, Object> planData) {
        String url = iPlanBaseUrl + "/api/planning";
        
        // Add kegiatan metadata to plan data
        planData.put("kegiatanCode", kegiatan.getCode());
        planData.put("kegiatanName", kegiatan.getName());
        planData.put("smsKegiatanId", kegiatan.getId());
        
        try {
            ResponseEntity<Map<String, Object>> response = 
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(planData, getHeaders()), 
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
            
            String errorMessage = "Error creating planning: " + response.getStatusCode();
            logger.error(errorMessage);
            return null;
        } catch (RestClientException e) {
            String errorMessage = "Error connecting to iPlan: " + e.getMessage();
            logger.error(errorMessage, e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public IntegrationTracking syncKegiatanWithIPlan(Long kegiatanId) {
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
            // If no external ID exists, try to create new planning in iPlan
            if (tracking.getExternalId() == null) {
                Map<String, Object> planData = new HashMap<>();
                planData.put("name", kegiatan.getName());
                planData.put("code", kegiatan.getCode());
                // Add other relevant data

                String externalId = createKegiatanPlanning(kegiatan, planData);
                if (externalId != null) {
                    tracking.setExternalId(externalId);
                    tracking.setSyncStatus("SUCCESS");
                    tracking.setErrorMessage(null);
                } else {
                    tracking.setSyncStatus("FAILED");
                    tracking.setErrorMessage("Failed to create planning in iPlan");
                }
            } else {
                // Get planning data to check if it exists
                Map<String, Object> planningData = getKegiatanPlanningData(kegiatanId);
                if (!planningData.containsKey("error")) {
                    tracking.setSyncStatus("SUCCESS");
                    tracking.setErrorMessage(null);
                    
                    // Save hash of data for future comparison
                    try {
                        String dataHash = objectMapper.writeValueAsString(planningData).hashCode() + "";
                        tracking.setDataHash(dataHash);
                    } catch (JsonProcessingException e) {
                        logger.warn("Failed to create hash for planning data", e);
                    }
                } else {
                    tracking.setSyncStatus("FAILED");
                    tracking.setErrorMessage((String) planningData.get("message"));
                }
            }
        } catch (Exception e) {
            tracking.setSyncStatus("FAILED");
            tracking.setErrorMessage("Error synchronizing with iPlan: " + e.getMessage());
            logger.error("Error synchronizing with iPlan for kegiatan ID: {}", kegiatanId, e);
        }
        
        return trackingRepository.save(tracking);
    }
    
    @Override
    public List<Map<String, Object>> getPlanningDocuments(Long kegiatanId) {
        Optional<String> externalIdOpt = findExternalIPlanId(kegiatanId);
        if (externalIdOpt.isEmpty()) {
            logger.warn("No external iPlan ID found for kegiatan ID: {}", kegiatanId);
            return Collections.emptyList();
        }
        
        String externalId = externalIdOpt.get();
        String url = iPlanBaseUrl + "/api/planning/" + externalId + "/documents";
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = 
                restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), 
                                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
            logger.warn("Error fetching planning documents: {}", response.getStatusCode());
            return Collections.emptyList();
        } catch (RestClientException e) {
            logger.error("Error connecting to iPlan: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<IPlanDataDto> getPlanningDataByYear(String year) {
        // First try direct API call
        String url = iPlanBaseUrl + "/api/planning/year/" + year;
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = 
                restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), 
                                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().stream()
                    .map(data -> {
                        try {
                            String json = objectMapper.writeValueAsString(data);
                            return objectMapper.readValue(json, IPlanDataDto.class);
                        } catch (JsonProcessingException e) {
                            logger.error("Error converting planning data", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
        } catch (RestClientException e) {
            logger.warn("REST API call to iPlan failed, falling back to direct database query: {}", e.getMessage());
        }
        
        // Fallback to direct database query
        try {
            List<Map<String, Object>> planningDataList = iPlanDatabaseService.getPlannedKegiatanByYear(Integer.parseInt(year));
            
            return planningDataList.stream()
                .map(data -> {
                    try {
                        String json = objectMapper.writeValueAsString(data);
                        return objectMapper.readValue(json, IPlanDataDto.class);
                    } catch (JsonProcessingException e) {
                        logger.error("Error converting planning data from database", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error querying iPlan database", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Optional<String> findExternalIPlanId(Long kegiatanId) {
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