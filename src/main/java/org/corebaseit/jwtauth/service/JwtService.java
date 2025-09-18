package org.corebaseit.jwtauth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final String issuer;
    private final SecretKey key;
    private final long accessMillis;
    private final long refreshMillis;

    public JwtService(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.accessMinutes}") long accessMinutes,
            @Value("${app.jwt.refreshDays}") long refreshDays
    ) {
        this.issuer = issuer;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMillis  = accessMinutes * 60_000L;
        this.refreshMillis = refreshDays * 24L * 60L * 60L * 1000L;
    }

    public String generateAccessToken(String subject, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessMillis))
                .claims(extraClaims == null ? Map.of() : extraClaims)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshMillis))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getSubject(String jwt) {
        var claims = Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims.getSubject();
    }

    public boolean isAccessTokenValid(String jwt) {
        try {
            Jwts.parser()
                    .requireIssuer(issuer)
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}