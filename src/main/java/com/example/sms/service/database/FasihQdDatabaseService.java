package com.example.sms.service.database;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class FasihQdDatabaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(FasihQdDatabaseService.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public FasihQdDatabaseService(@Qualifier("fasihQdJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<Map<String, Object>> getQuestionnairesByKegiatanId(Long kegiatanId) {
        String sql = "SELECT * FROM questionnaires WHERE sms_kegiatan_id = ?";
        
        try {
            return jdbcTemplate.queryForList(sql, kegiatanId);
        } catch (DataAccessException e) {
            logger.error("Error fetching questionnaires for kegiatan ID: {}", kegiatanId, e);
            return Collections.emptyList();
        }
    }
    
    public Map<String, Object> getQuestionnaireById(String questionnaireId) {
        String sql = "SELECT * FROM questionnaires WHERE id = ?";
        
        try {
            return jdbcTemplate.queryForMap(sql, questionnaireId);
        } catch (DataAccessException e) {
            logger.error("Error fetching questionnaire with ID: {}", questionnaireId, e);
            return Collections.emptyMap();
        }
    }
    
    public List<Map<String, Object>> getQuestionsByQuestionnaireId(String questionnaireId) {
        String sql = "SELECT * FROM questions WHERE questionnaire_id = ? ORDER BY position";
        
        try {
            return jdbcTemplate.queryForList(sql, questionnaireId);
        } catch (DataAccessException e) {
            logger.error("Error fetching questions for questionnaire ID: {}", questionnaireId, e);
            return Collections.emptyList();
        }
    }
    
    public List<Map<String, Object>> getDesignHistoryByQuestionnaireId(String questionnaireId) {
        String sql = "SELECT * FROM design_history WHERE questionnaire_id = ? ORDER BY timestamp DESC";
        
        try {
            return jdbcTemplate.queryForList(sql, questionnaireId);
        } catch (DataAccessException e) {
            logger.error("Error fetching design history for questionnaire ID: {}", questionnaireId, e);
            return Collections.emptyList();
        }
    }
    
    public List<Map<String, Object>> getQuestionnaireTemplates() {
        String sql = "SELECT * FROM questionnaire_templates WHERE active = 1";
        
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            logger.error("Error fetching questionnaire templates", e);
            return Collections.emptyList();
        }
    }
    
    public boolean updateQuestionnaireStatus(String questionnaireId, String status) {
        String sql = "UPDATE questionnaires SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, status, questionnaireId);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Error updating questionnaire status for ID: {}", questionnaireId, e);
            return false;
        }
    }
    
    public Map<String, Object> getDesignProgressMetrics(String questionnaireId) {
        String sql = "CALL GetDesignProgressMetrics(?)";
        
        try {
            return jdbcTemplate.queryForMap(sql, questionnaireId);
        } catch (DataAccessException e) {
            logger.error("Error fetching design progress metrics for questionnaire ID: {}", questionnaireId, e);
            return Collections.emptyMap();
        }
    }
    
    public boolean doesQuestionnaireExist(String questionnaireId) {
        String sql = "SELECT COUNT(*) FROM questionnaires WHERE id = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, questionnaireId);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Error checking if questionnaire exists with ID: {}", questionnaireId, e);
            return false;
        }
    }
    
    public List<Map<String, Object>> searchQuestionnaires(String keyword) {
        String sql = "SELECT * FROM questionnaires WHERE name LIKE ? OR code LIKE ? ORDER BY updated_at DESC LIMIT 50";
        String searchParam = "%" + keyword + "%";
        
        try {
            return jdbcTemplate.queryForList(sql, searchParam, searchParam);
        } catch (DataAccessException e) {
            logger.error("Error searching questionnaires with keyword: {}", keyword, e);
            return Collections.emptyList();
        }
    }
}