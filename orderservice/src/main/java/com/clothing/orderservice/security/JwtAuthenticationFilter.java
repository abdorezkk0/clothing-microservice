package com.clothing.orderservice.security;

import com.clothing.orderservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        System.out.println("🔐 [OrderService] JwtAuthenticationFilter initialized");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        System.out.println("🔐 [OrderService] ========================================");
        System.out.println("🔐 [OrderService] JWT Filter: Processing request");
        System.out.println("🔐 [OrderService] URI: " + request.getRequestURI());
        System.out.println("🔐 [OrderService] Method: " + request.getMethod());

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            System.out.println("🔐 [OrderService] Authorization header: Present");
            System.out.println("🔐 [OrderService] Header value (first 30 chars): " + authorizationHeader.substring(0, Math.min(30, authorizationHeader.length())) + "...");
            
            jwt = authorizationHeader.substring(7);
            System.out.println("🔐 [OrderService] JWT Token extracted");
            System.out.println("🔐 [OrderService] Token length: " + jwt.length());
            System.out.println("🔐 [OrderService] Token (first 50 chars): " + jwt.substring(0, Math.min(50, jwt.length())) + "...");

            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("🔐 [OrderService] Extracted username: " + username);
            } catch (Exception e) {
                System.err.println("❌ [OrderService] Error extracting username: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ [OrderService] No Authorization header or invalid format");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("🔐 [OrderService] Validating token for user: " + username);

            try {
                if (jwtUtil.validateToken(jwt, username)) {
                    System.out.println("✅ [OrderService] Token is VALID!");

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ [OrderService] Authentication SET in SecurityContext");
                    System.out.println("👤 [OrderService] User: " + username);
                    System.out.println("🔐 [OrderService] Authorities: " + authToken.getAuthorities());
                } else {
                    System.err.println("❌ [OrderService] Token validation FAILED!");
                }
            } catch (Exception e) {
                System.err.println("❌ [OrderService] Token validation error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("🔐 [OrderService] ========================================");
        chain.doFilter(request, response);
    }
}