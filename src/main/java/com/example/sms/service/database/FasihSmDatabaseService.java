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
                "GROUP BY e.id, e.name, e.region_code";
        
        try {
            return jdbcTemplate.queryForList(sql, surveyId, surveyId);
        } catch (DataAccessException e) {
            logger.error("Error fetching enumerator performance for survey ID: {}", surveyId, e);
            return Collections.emptyList();
        }
    }
    
    public Map<String, Object> getDataQualityMetrics(String surveyId) {
        try {
            // Get basic data quality metrics
            String metricsSql = "SELECT " +
                    "COUNT(DISTINCT ss.id) as total_responses, " +
                    "AVG(dr.completion_time_seconds) as avg_completion_time, " +
                    "SUM(CASE WHEN dr.has_validation_errors > 0 THEN 1 ELSE 0 END) as responses_with_errors, " +
                    "SUM(CASE WHEN dr.has_outliers > 0 THEN 1 ELSE 0 END) as responses_with_outliers, " +
                    "SUM(dr.missing_values) as total_missing_values " +
                    "FROM survey_samples ss " +
                    "JOIN data_responses dr ON ss.id = dr.sample_id " +
                    "WHERE ss.survey_id = ? AND ss.status = 'COMPLETED'";
            
            Map<String, Object> metrics = jdbcTemplate.queryForMap(metricsSql, surveyId);
            
            // Get question-level quality metrics
            String questionSql = "SELECT " +
                    "q.id as question_id, " +
                    "q.text as question_text, " +
                    "COUNT(DISTINCT dr.id) as total_responses, " +
                    "SUM(CASE WHEN drq.has_error > 0 THEN 1 ELSE 0 END) as error_count, " +
                    "SUM(CASE WHEN drq.is_missing > 0 THEN 1 ELSE 0 END) as missing_count, " +
                    "AVG(drq.response_time_seconds) as avg_response_time " +
                    "FROM questions q " +
                    "JOIN data_response_questions drq ON q.id = drq.question_id " +
                    "JOIN data_responses dr ON drq.response_id = dr.id " +
                    "JOIN survey_samples ss ON dr.sample_id = ss.id " +
                    "WHERE ss.survey_id = ? " +
                    "GROUP BY q.id, q.text " +
                    "ORDER BY error_count DESC";
            
            List<Map<String, Object>> questionMetrics = jdbcTemplate.queryForList(questionSql, surveyId);
            
            // Combine results
            Map<String, Object> result = new HashMap<>();
            result.put("surveyMetrics", metrics);
            result.put("questionMetrics", questionMetrics);
            
            return result;
        } catch (DataAccessException e) {
            logger.error("Error fetching data quality metrics for survey ID: {}", surveyId, e);
            return Collections.emptyMap();
        }
    }
    
    public String getProcessedDataUrl(String surveyId) {
        String sql = "SELECT processed_data_url FROM surveys WHERE id = ? AND status = 'COMPLETED'";
        
        try {
            return jdbcTemplate.queryForObject(sql, String.class, surveyId);
        } catch (DataAccessException e) {
            logger.error("Error fetching processed data URL for survey ID: {}", surveyId, e);
            return null;
        }
    }
    
    public boolean updateSurveyStatus(String surveyId, String status) {
        String sql = "UPDATE surveys SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, status, surveyId);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Error updating survey status for ID: {}", surveyId, e);
            return false;
        }
    }
    
    public Map<String, Object> getSurveySummaryMetrics(String surveyId) {
        String sql = "SELECT " +
                "s.name as survey_name, " +
                "s.code as survey_code, " +
                "s.status as survey_status, " +
                "s.start_date, " +
                "s.end_date, " +
                "COUNT(DISTINCT ss.id) as total_samples, " +
                "SUM(CASE WHEN ss.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_samples, " +
                "COUNT(DISTINCT e.id) as total_enumerators, " +
                "COUNT(DISTINCT ss.region_code) as region_count " +
                "FROM surveys s " +
                "LEFT JOIN survey_samples ss ON s.id = ss.survey_id " +
                "LEFT JOIN enumerators e ON s.id = e.survey_id " +
                "WHERE s.id = ? " +
                "GROUP BY s.id, s.name, s.code, s.status, s.start_date, s.end_date";
        
        try {
            return jdbcTemplate.queryForMap(sql, surveyId);
        } catch (DataAccessException e) {
            logger.error("Error fetching survey summary metrics for ID: {}", surveyId, e);
            return Collections.emptyMap();
        }
    }
    
    public List<Map<String, Object>> getDailySurveyProgress(String surveyId) {
        String sql = "SELECT " +
                "DATE(ss.completed_at) as date, " +
                "COUNT(*) as completed_count " +
                "FROM survey_samples ss " +
                "WHERE ss.survey_id = ? AND ss.status = 'COMPLETED' AND ss.completed_at IS NOT NULL " +
                "GROUP BY DATE(ss.completed_at) " +
                "ORDER BY DATE(ss.completed_at)";
        
        try {
            return jdbcTemplate.queryForList(sql, surveyId);
        } catch (DataAccessException e) {
            logger.error("Error fetching daily survey progress for ID: {}", surveyId, e);
            return Collections.emptyList();
        }
    }
    
    public boolean doesSurveyExist(String surveyId) {
        String sql = "SELECT COUNT(*) FROM surveys WHERE id = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, surveyId);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Error checking if survey exists with ID: {}", surveyId, e);
            return false;
        }
    }
    
    public List<Map<String, Object>> searchSurveys(String keyword) {
        String sql = "SELECT * FROM surveys WHERE name LIKE ? OR code LIKE ? ORDER BY updated_at DESC LIMIT 50";
        String searchParam = "%" + keyword + "%";
        
        try {
            return jdbcTemplate.queryForList(sql, searchParam, searchParam);
        } catch (DataAccessException e) {
            logger.error("Error searching surveys with keyword: {}", keyword, e);
            return Collections.emptyList();
        }
    }
}