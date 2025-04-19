package com.example.sms.controller.api;

import com.example.sms.entity.User;
import com.example.sms.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling API authentication
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Login endpoint for API
     * 
     * @param loginRequest contains email and password
     * @return user information on success
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || password == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email and password are required"));
        }

        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userService.findUserByEmail(email);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("roles", user.getRoles());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials", "message", e.getMessage()));
        }
    }
}