package com.example.sms.dto.integration;

import java.time.LocalDate;
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
public class IPlanDataDto {
    private Long iPlanId;
    private String kegiatanCode;
    private String kegiatanName;
    private String status;
    private String year;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private String budgetAllocation;
    private List<PlanningObjectiveDto> objectives;
    private List<PlanningStakeholderDto> stakeholders;
    private List<DocumentDto> documents;
    private Map<String, Object> metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanningObjectiveDto {
        private String id;
        private String description;
        private String measurableTarget;
        private String category;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanningStakeholderDto {
        private String id;
        private String name;
        private String role;
        private String organization;
        private String contactInfo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentDto {
        private String id;
        private String name;
        private String type;
        private String url;
        private LocalDate uploadDate;
    }
}