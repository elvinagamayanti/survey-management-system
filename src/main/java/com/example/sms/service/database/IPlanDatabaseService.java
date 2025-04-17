package com.example.sms.service.database;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class IPlanDatabaseService {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public IPlanDatabaseService(@Qualifier("iPlanJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<Map<String, Object>> getPlannedKegiatanByYear(int year) {
        String sql = "SELECT * FROM iplan_kegiatan WHERE tahun = ?";
        return jdbcTemplate.queryForList(sql, year);
    }
    
    // Other methods to interact with iPlan database
}
