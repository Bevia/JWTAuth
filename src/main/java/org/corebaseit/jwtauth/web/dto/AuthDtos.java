package org.corebaseit.jwtauth.web.dto;


public class AuthDtos {
    public static class LoginRequest {
        private String username;
        private String password;
        // getters/setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private long   expiresIn; // seconds
        // ctor + getters/setters
        public LoginResponse() {}
        public LoginResponse(String accessToken, String refreshToken, long expiresIn) {
            this.accessToken = accessToken; this.refreshToken = refreshToken; this.expiresIn = expiresIn;
        }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String v) { this.accessToken = v; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String v) { this.refreshToken = v; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long v) { this.expiresIn = v; }
    }

    public static class RefreshRequest {
        private String refreshToken;
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}