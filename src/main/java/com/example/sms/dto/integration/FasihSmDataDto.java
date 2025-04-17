package com.example.sms.dto.integration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FasihSmDataDto {
    private Long fasihSmId;
    private String kegiatanCode;
    private String surveyCode;
    private String surveyName;
    private String status;
    private LocalDate collectionStartDate;
    private LocalDate collectionEndDate;
    private String questionnaireVersion;
    private Integer totalSamples;
    private Integer completedSamples;
    private Double completionRate;
    private List<SurveyRegionDto> regions;
    private List<EnumeratorDto> enumerators;
    private List<SurveyLogDto> surveyLogs;
    private Map<String, Object> metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyRegionDto {
        private String id;
        private String regionCode;
        private String regionName;
        private Integer targetSamples;
        private Integer completedSamples;
        private Double completionRate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnumeratorDto {
        private String id;
        private String name;
        private String region;
        private Integer assignedSamples;
        private Integer completedSamples;
        private String status;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyLogDto {
        private Long id;
        private String action;
        private LocalDateTime timestamp;
        private String username;
        private String description;
    }
}