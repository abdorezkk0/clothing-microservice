package com.clothing.api_gateway.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtUtil jwtUtil;

    // ✅ Public endpoints that don't require authentication
    private static final List<String> PUBLIC_PATHS = List.of(
        "/userservice/api/auth/login",
        "/userservice/api/auth/register",
        "/userservice/api/users/register",
        "/actuator"  // Gateway actuator
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        logger.info("[Gateway] Incoming request: {} {}", method, path);

        // Allow public paths
        if (isPublicPath(path, method)) {
            logger.info("[Gateway] ✅ Public path, skipping auth: {}", path);
            return chain.filter(exchange);
        }

        // Extract token
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("[Gateway] ❌ Missing or invalid Authorization header for: {}", path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        
        try {
            // Validate token
            if (!jwtUtil.isTokenValid(token)) {
                logger.warn("[Gateway] ❌ Invalid or expired token for: {}", path);
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Extract username and add to headers for downstream services
            String username = jwtUtil.extractUsername(token);
            logger.info("[Gateway] ✅ Valid token for user: {}", username);

            // Add username to header for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", username)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            logger.error("[Gateway] ❌ Token validation error: {}", e.getMessage());
            return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicPath(String path, String method) {
        // ✅ Allow all actuator endpoints (any service)
        if (path.contains("/actuator/")) {
            logger.info("[Gateway] ✅ Actuator endpoint, skipping auth: {}", path);
            return true;
        }
        
        // ✅ Allow GET requests to products (browsing is public)
        if (method.equals("GET") && path.startsWith("/productservice/api/products")) {
            logger.info("[Gateway] ✅ Public GET request to products");
            return true;
        }
        
        // Check if path starts with any public path
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            logger.info("[Gateway] ✅ Path matches public list: {}", path);
        }
        return isPublic;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorJson = String.format(
            "{\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\",\"timestamp\":\"%s\"}", 
            status.getReasonPhrase(),
            message,
            exchange.getRequest().getPath().value(),
            java.time.Instant.now().toString()
        );
        
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(errorJson.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -100; // High priority - run before other filters
    }
}