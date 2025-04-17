package com.example.sms.service.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FasihSmDatabaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(FasihSmDatabaseService.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public FasihSmDatabaseService(@Qualifier("fasihSmJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<Map<String, Object>> getSurveysByKegiatanId(Long kegiatanId) {
        String sql = "SELECT * FROM surveys WHERE sms_kegiatan_id = ?";
        
        try {
            return jdbcTemplate.queryForList(sql, kegiatanId);
        } catch (DataAccessException e) {
            logger.error("Error fetching surveys for kegiatan ID: {}", kegiatanId, e);
            return Collections.emptyList();
        }
    }
    
    public Map<String, Object> getSurveyById(String surveyId) {
        String sql = "SELECT * FROM surveys WHERE id = ?";
        
        try {
            return jdbcTemplate.queryForMap(sql, surveyId);
        } catch (DataAccessException e) {
            logger.error("Error fetching survey with ID: {}", surveyId, e);
            return Collections.emptyMap();
        }
    }
    
    public Map<String, Object> getSurveyStatistics(String surveyId) {
        try {
            // Get sample statistics
            String sampleSql = "SELECT COUNT(*) as total_samples, " +
                    "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_samples, " +
                    "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_samples, " +
                    "SUM(CASE WHEN status = 'NOT_STARTED' THEN 1 ELSE 0 END) as not_started_samples " +
                    "FROM survey_samples WHERE survey_id = ?";
            
            Map<String, Object> sampleStats = jdbcTemplate.queryForMap(sampleSql, surveyId);
            
            // Get region statistics
            String regionSql = "SELECT region_code, region_name, " +
                    "COUNT(*) as total_samples, " +
                    "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_samples " +
                    "FROM survey_samples WHERE survey_id = ? " +
                    "GROUP BY region_code, region_name";
            
            List<Map<String, Object>> regionStats = jdbcTemplate.queryForList(regionSql, surveyId);
            
            // Combine results
            Map<String, Object> result = new HashMap<>();
            result.put("sampleStatistics", sampleStats);
            result.put("regionStatistics", regionStats);
            
            return result;
        } catch (DataAccessException e) {
            logger.error("Error fetching survey statistics for ID: {}", surveyId, e);
            return Collections.emptyMap();
        }
    }
    
    public List<Map<String, Object>> getEnumeratorPerformance(String surveyId) {
        String sql = "SELECT e.id, e.name, e.region_code, " +
                "COUNT(ss.id) as assigned_samples, " +
                "SUM(CASE WHEN ss.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_samples, " +
                "AVG(CASE WHEN ss.status = 'COMPLETED' THEN TIMESTAMPDIFF(MINUTE, ss.start_time, ss.end_time) ELSE NULL END) as avg_completion_time_minutes " +
                "FROM enumerators e " +
                "LEFT JOIN survey_samples ss ON e.id = ss.enumerator_id AND ss.survey_id = ? " +
                "WHERE e.survey_id = ? " +
                "GROUP BY e.id, e.name, e.