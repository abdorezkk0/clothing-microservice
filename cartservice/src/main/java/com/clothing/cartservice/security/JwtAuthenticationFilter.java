package com.clothing.cartservice.security;

import com.clothing.cartservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("\n🛒 [CartService] ========================================");
        System.out.println("🛒 [CartService] JWT Filter: Processing request");
        System.out.println("🛒 [CartService] URI: " + requestURI);
        System.out.println("🛒 [CartService] Method: " + method);

        final String authHeader = request.getHeader("Authorization");
        System.out.println("🛒 [CartService] Authorization header: " + (authHeader != null ? "Present" : "❌ MISSING"));
        
        if (authHeader != null) {
            System.out.println("🛒 [CartService] Header value (first 30 chars): " + authHeader.substring(0, Math.min(30, authHeader.length())) + "...");
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ [CartService] No valid Authorization header - allowing through (will be rejected by Spring Security)");
            System.out.println("🛒 [CartService] ========================================\n");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            System.out.println("🔑 [CartService] JWT Token extracted");
            System.out.println("🔑 [CartService] Token length: " + jwt.length());
            System.out.println("🔑 [CartService] Token (first 50 chars): " + jwt.substring(0, Math.min(50, jwt.length())) + "...");
            
            final String username = jwtUtil.extractUsername(jwt);
            System.out.println("👤 [CartService] Extracted username: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("🔍 [CartService] Validating token...");
                
                if (jwtUtil.validateToken(jwt)) {
                    System.out.println("✅ [CartService] Token is VALID!");

                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_USER")
                    );

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ [CartService] Authentication SET in SecurityContext");
                    System.out.println("👤 [CartService] User: " + username);
                    System.out.println("🔐 [CartService] Authorities: " + authorities);
                } else {
                    System.err.println("❌ [CartService] Token validation FAILED");
                }
            } else {
                if (username == null) {
                    System.err.println("❌ [CartService] Username extraction FAILED");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("ℹ️ [CartService] Authentication already present in context");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [CartService] JWT Filter EXCEPTION: " + e.getClass().getSimpleName());
            System.err.println("❌ [CartService] Error message: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("🛒 [CartService] ========================================\n");
        filterChain.doFilter(request, response);
    }
}