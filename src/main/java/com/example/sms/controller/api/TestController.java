package com.example.sms.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Extremely simple controller to test REST capabilities
 */
@RestController
public class TestController {

    /**
     * Basic test endpoint to verify REST is working
     */
    @GetMapping(value = "/test-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String testJson() {
        return "{\"message\":\"This is a test JSON response\"}";
    }
}