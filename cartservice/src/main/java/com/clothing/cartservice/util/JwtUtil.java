package com.clothing.cartservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public JwtUtil() {
        System.out.println("🔧 [CartService] JwtUtil created");
    }

    private SecretKey getSigningKey() {
        // Print the secret being used (first time only)
        if (secret != null) {
            System.out.println("🔑 [CartService] JWT Secret loaded from config:");
            System.out.println("🔑 [CartService] Secret length: " + secret.length());
            System.out.println("🔑 [CartService] Secret (first 20 chars): " + secret.substring(0, Math.min(20, secret.length())) + "...");
            System.out.println("🔑 [CartService] Secret (last 20 chars): ..." + secret.substring(Math.max(0, secret.length() - 20)));
        } else {
            System.err.println("❌ [CartService] JWT Secret is NULL!");
        }
        
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
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
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}