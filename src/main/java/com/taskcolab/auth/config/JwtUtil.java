
package com.taskcolab.auth.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity}")
    private long ACCESS_TOKEN_VALIDITY;

    @Value("${jwt.refresh-token-validity}")
    private long REFRESH_TOKEN_VALIDITY;

    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Lazily initialize the SecretKey
    private SecretKey getSigningKey() {
        try {
            // La clé est encodée en Base64 URL-safe, comme dans JwtUtils
            byte[] keyBytes = Base64.getUrlDecoder().decode(secret);
            if (keyBytes.length < 64) {
                throw new IllegalArgumentException("La clé secrète est trop courte pour HS512. Elle doit être d'au moins 64 octets.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors du décodage de la clé secrète : {}", e.getMessage());
            throw e;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(cleanToken(token), Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(cleanToken(token), Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(cleanToken(token), claims -> claims.get("type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(cleanToken(token));
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(cleanToken(token))
                    .getBody();
        } catch (JwtException e) {
            System.err.println("Erreur lors de l'extraction des claims : " + e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(cleanToken(token)).before(new Date());
        } catch (Exception e) {
            return true; // Consider expired if token is invalid
        }
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return createToken(claims, username, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, REFRESH_TOKEN_VALIDITY);
    }

    private String createToken(Map<String, Object> claims, String subject, long validity) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(cleanToken(token));
            return (extractedUsername.equals(username) && !isTokenExpired(cleanToken(token)));
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    public Boolean isRefreshToken(String token) {
        try {
            String tokenType = extractTokenType(cleanToken(token));
            System.out.println("Type extrait : " + tokenType);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            System.err.println("Erreur dans isRefreshToken : " + e.getMessage());
            return false;
        }
    }

    private String cleanToken(String token) {
        if (token == null) {
            return null;
        }
        // Supprime les guillemets et les espaces en début/fin
        return token.trim().replaceAll("^\"|\"$", "");
    }
}