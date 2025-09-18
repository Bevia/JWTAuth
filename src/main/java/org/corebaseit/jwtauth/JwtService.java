package org.corebaseit.jwtauth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final String issuer;
    private final Key hmacKey;
    private final long accessMinutes;
    private final long refreshDays;

    public JwtService(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.accessMinutes}") long accessMinutes,
            @Value("${app.jwt.refreshDays}") long refreshDays
    ) {
        this.issuer = issuer;
        // Accept raw or base64 secret
        byte[] keyBytes = secret.matches("^[A-Za-z0-9+/=]+$") ? Decoders.BASE64.decode(secret)
                : secret.getBytes();
        this.hmacKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plus(accessMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plus(refreshDays, ChronoUnit.DAYS);
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .addClaims(claims)
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(parse(token).getBody().get("type"));
        } catch (JwtException e) {
            return false;
        }
    }

    public long getAccessTtlSeconds() {
        return accessMinutes * 60;
    }
}