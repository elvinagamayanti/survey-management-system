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
public class FasihQdDataDto {
    private Long fasihQdId;
    private String kegiatanCode;
    private String questionnaireCode;
    private String questionnaireName;
    private String status;
    private LocalDate designStartDate;
    private LocalDate designEndDate;
    private String version;
    private List<QuestionnaireBlockDto> blocks;
    private List<DesignStageDto> designStages;
    private List<DesignTeamMemberDto> designTeam;
    private Map<String, Object> metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionnaireBlockDto {
        private String id;
        private String name;
        private String type;
        private Integer questionCount;
        private Integer logicRuleCount;
        private String status;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DesignStageDto {
        private String id;
        private String name;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String completedBy;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DesignTeamMemberDto {
        private String id;
        private String name;
        private String role;
        private String email;
    }
}