package org.corebaseit.jwtauth.web;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.accessMinutes}")
    private int accessMinutes;

    @Value("${app.jwt.refreshDays}")
    private int refreshDays;

    // In-memory "refresh token store" (for demo only!)
    private final Map<String, String> refreshTokens = new HashMap<>();

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // Super simple check (replace with real user validation)
        if (!"user".equals(username) || !"password".equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = createToken(username, accessMinutes * 60 * 1000);
        String refreshToken = createToken(username, refreshDays * 24 * 60 * 60 * 1000);

        refreshTokens.put(refreshToken, username);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (!refreshTokens.containsKey(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = refreshTokens.get(refreshToken);
        String newAccessToken = createToken(username, accessMinutes * 60 * 1000);

        return Map.of("accessToken", newAccessToken);
    }

    private String createToken(String subject, long validityMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validityMs))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }
}