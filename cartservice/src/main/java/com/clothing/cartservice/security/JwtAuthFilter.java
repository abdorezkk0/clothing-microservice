package com.clothing.cartservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("\n🔐 JwtAuthFilter - Processing request:");
        System.out.println("   Path: " + request.getRequestURI());
        System.out.println("   Method: " + request.getMethod());
        System.out.println("   Auth Header: " + (authHeader != null ? "Present" : "Missing"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("   Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            try {
                username = jwtService.extractUsername(token);
                System.out.println("   Username from token: " + username);
            } catch (Exception e) {
                System.err.println("   ❌ Failed to extract username: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(token)) {
                System.out.println("   ✅ Token is valid");
                
                // ✅ Grant USER authority
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                System.out.println("   ✅ Authentication set with ROLE_USER");
            } else {
                System.err.println("   ❌ Token is invalid or expired");
            }
        }

        filterChain.doFilter(request, response);
    }
}