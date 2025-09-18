package org.corebaseit.jwtauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.corebaseit.jwtauth.dto.AuthDtos.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwt;

    public AuthController(JwtService jwt) {
        this.jwt = jwt;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        // 🔒 TODO: replace with real credentials check
        if (req.username == null || req.password == null) {
            return ResponseEntity.badRequest().build();
        }

        // Example claims (add what you need: tid, roles, etc.)
        Map<String, Object> claims = Map.of("tid", "00000URB", "roles", "POS");

        String access  = jwt.generateAccessToken(req.username, claims);
        String refresh = jwt.generateRefreshToken(req.username, Map.of("tid", "00000URB"));

        return ResponseEntity.ok(new TokenResponse(access, refresh, jwt.getAccessTtlSeconds()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody(required = false) RefreshRequest body
    ) {
        String token = extractBearer(authHeader);
        if (token == null && body != null) token = body.refreshToken;
        if (token == null || token.isBlank()) return ResponseEntity.badRequest().build();

        if (!jwt.isRefreshToken(token)) return ResponseEntity.status(400).build();

        try {
            Jws<io.jsonwebtoken.Claims> jws = jwt.parse(token);
            Claims c = jws.getBody();
            String sub = c.getSubject();
            String tid = c.get("tid", String.class);

            String newAccess  = jwt.generateAccessToken(sub, Map.of("tid", tid, "roles", "POS"));
            String newRefresh = jwt.generateRefreshToken(sub, Map.of("tid", tid));

            return ResponseEntity.ok(new TokenResponse(newAccess, newRefresh, jwt.getAccessTtlSeconds()));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    private String extractBearer(String header) {
        if (header == null) return null;
        if (!header.startsWith("Bearer ")) return null;
        return header.substring("Bearer ".length()).trim();
    }
}