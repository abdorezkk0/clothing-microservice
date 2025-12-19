package com.clothing.userservice.controller;

import java.util.Map;

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
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        System.out.println("\n🔐 Login attempt:");
        System.out.println("   Username: " + username);
        System.out.println("   Password provided: " + (password != null));

        try {
            // ✅ Authenticate credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // ✅ Extract user details and generate token
            UserDetails user = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            System.out.println("✅ Login successful for user: " + username);
            System.out.println("🪪 Token prefix: " + token.substring(0, 12) + "...");

            return Map.of(
                "token", token,
                "user", user.getUsername(),
                "roles", user.getAuthorities(),
                "message", "Login successful"
            );

        } catch (AuthenticationException e) {
            System.err.println("❌ Authentication failed: " + e.getMessage());
            return Map.of(
                "error", "INVALID_CREDENTIALS",
                "message", "Username or password is incorrect"
            );

        } catch (Exception e) {
            System.err.println("💥 Unexpected error during login: " + e.getMessage());
            return Map.of(
                "error", "LOGIN_ERROR",
                "message", e.getMessage()
            );
        }
    }
}
