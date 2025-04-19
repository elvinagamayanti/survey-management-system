package com.example.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.MediaType;

/**
 * Web MVC configuration to ensure proper content negotiation
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure content negotiation to prioritize JSON for API responses
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("html", MediaType.TEXT_HTML);
    }
}