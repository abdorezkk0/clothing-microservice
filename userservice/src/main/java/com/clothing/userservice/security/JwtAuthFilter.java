package com.clothing.userservice.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);


    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

   @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

    final String path = request.getServletPath();
    final String method = request.getMethod();

    if (path.startsWith("/api/auth")
        || path.startsWith("/api/users/register")
        || (method.equals("GET") && path.startsWith("/api/products"))
        || path.startsWith("/h2-console")) {
        logger.debug("[JWT] Skipping filter for public path: {} {}", method, path);
        chain.doFilter(request, response);
        return;
    }

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        logger.debug("[JWT] Missing/invalid Authorization header for: {} {}", method, path);
        chain.doFilter(request, response);
        return;
    }

    String token = authHeader.substring(7);
    String safe = token.length() > 12 ? token.substring(0,6) + "..." + token.substring(token.length()-6) : "short-token";
    logger.debug("[JWT] Token received: {}", safe);

    String username = null;
    try {
        username = jwtService.extractUsername(token);
        logger.debug("[JWT] Username from token: {}", username);
    } catch (Exception e) {
        logger.warn("[JWT] Failed to extract username: {}", e.getMessage());
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        boolean valid = false;
        try {
            valid = jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            logger.warn("[JWT] Token validation error: {}", e.getMessage());
        }
        logger.debug("[JWT] Token valid? {}", valid);

        if (valid) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.debug("[JWT] SecurityContext set for user: {} with authorities: {}", username, userDetails.getAuthorities());
        } else {
            logger.debug("[JWT] Token invalid for user: {}", username);
        }
    }

    chain.doFilter(request, response);
}

}
