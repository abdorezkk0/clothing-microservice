package com.clothing.orderservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    public JwtUtil() {
        System.out.println("📋 [OrderService] JwtUtil created");
    }

    public String extractUsername(String token) {
        System.out.println("🔍 [OrderService] Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        System.out.println("🔐 [OrderService] JWT Secret loaded from config:");
        System.out.println("🔐 [OrderService] Secret length: " + secret.length());
        System.out.println("🔐 [OrderService] Secret (first 20 chars): " + secret.substring(0, Math.min(20, secret.length())) + "...");
        System.out.println("🔐 [OrderService] Secret (last 20 chars): ..." + secret.substring(Math.max(0, secret.length() - 20)));
        
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        System.out.println("🔐 [OrderService] Validating token...");
        final String extractedUsername = extractUsername(token);
        boolean isValid = (extractedUsername.equals(username) && !isTokenExpired(token));
        System.out.println("🔐 [OrderService] Token validation result: " + isValid);
        return isValid;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}