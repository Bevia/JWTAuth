package org.corebaseit.jwtauth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.corebaseit.jwtauth.web.dto.AuthDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final String issuer;
    private final Key key;
    private final long accessMinutes;
    private final long refreshDays;

    public JwtService(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.accessMinutes}") long accessMinutes,
            @Value("${app.jwt.refreshDays}") long refreshDays) {
        this.issuer = issuer;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    public AuthDtos.LoginResponse generateTokens(String subject) {
        var now = Instant.now();
        var accessExp = now.plusSeconds(accessMinutes * 60);
        var refreshExp = now.plusSeconds(refreshDays * 24 * 60 * 60);

        String at = Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(accessExp))
                .claim("typ", "access")
                .signWith(key)
                .compact();

        String rt = Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshExp))
                .claim("typ", "refresh")
                .signWith(key)
                .compact();

        long expiresIn = accessMinutes * 60;
        return new AuthDtos.LoginResponse(at, rt, expiresIn);
    }

    public AuthDtos.LoginResponse refresh(String refreshToken) {
        var parsed = Jwts.parser().verifyWith((javax.crypto.SecretKey) key).build().parseSignedClaims(refreshToken);
        var claims = parsed.getPayload();
        if (!"refresh".equals(claims.get("typ"))) {
            throw new IllegalArgumentException("Not a refresh token");
        }
        var user = claims.getSubject();
        return generateTokens(user);
    }
}