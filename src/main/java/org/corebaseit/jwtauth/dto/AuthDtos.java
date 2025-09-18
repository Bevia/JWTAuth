package org.corebaseit.jwtauth.dto;


public class AuthDtos {

    // POST /auth/login
    public static class LoginRequest {
        public String username;
        public String password;
    }

    // POST /auth/refresh
    public static class RefreshRequest {
        public String refreshToken; // optional if you also support Authorization header
    }

    public static class TokenResponse {
        public String accessToken;
        public String refreshToken;
        public long   expiresInSeconds;

        public TokenResponse(String accessToken, String refreshToken, long expiresInSeconds) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresInSeconds = expiresInSeconds;
        }
    }
}