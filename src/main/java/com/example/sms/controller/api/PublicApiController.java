package com.example.sms.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for public API endpoints that don't require authentication
 */
@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    /**
     * Test endpoint to verify API is working
     * 
     * @return simple message
     */
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> testApi() {
        System.out.println("PublicApiController.testApi() called");
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "REST API is working correctly");

        // Explicitly set content type
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}