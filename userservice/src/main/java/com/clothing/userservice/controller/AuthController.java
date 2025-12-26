package com.clothing.userservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clothing.userservice.security.JwtService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        System.out.println("\n🔐 Login attempt:");
        System.out.println("   Username: " + username);
        System.out.println("   Password provided: " + (password != null));

        try {
            // ✅ Authenticate credentials (password comparison happens here automatically)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // ✅ Extract user details and generate token
            UserDetails user = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            System.out.println("✅ Login successful for user: " + username);
            System.out.println("🪪 Token generated successfully");

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            System.err.println("❌ Authentication failed: " + e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INVALID_CREDENTIALS");
            error.put("message", "Username or password is incorrect");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (Exception e) {
            System.err.println("💥 Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "LOGIN_ERROR");
            error.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}