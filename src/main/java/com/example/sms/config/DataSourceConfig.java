package com.example.sms.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.sms")
    public DataSource smsDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "iPlanDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.iplan")
    public DataSource iPlanDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "fasihQdDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.fasih-qd")
    public DataSource fasihQdDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "fasihSmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.fasih-sm")
    public DataSource fasihSmDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "iPlanJdbcTemplate")
    public JdbcTemplate iPlanJdbcTemplate(@Qualifier("iPlanDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    @Bean(name = "fasihQdJdbcTemplate")
    public JdbcTemplate fasihQdJdbcTemplate(@Qualifier("fasihQdDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    @Bean(name = "fasihSmJdbcTemplate")
    public JdbcTemplate fasihSmJdbcTemplate(@Qualifier("fasihSmDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}