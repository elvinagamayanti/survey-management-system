package com.example.sms.service.integration;

import java.util.Map;

public interface IPlanService {
    Map<String, Object> getKegiatanPlanningData(Long kegiatanId);
    boolean updateKegiatanPlanningStatus(Long kegiatanId, String status);
    // Other methods for iPlan integration
}