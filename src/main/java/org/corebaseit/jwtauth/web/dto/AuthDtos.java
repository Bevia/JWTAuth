package org.corebaseit.jwtauth.web.dto;


import lombok.Getter;
import lombok.Setter;

public class AuthDtos {
    @Setter
    @Getter
    public static class LoginRequest {
        // getters/setters
        private String username;
        private String password;

    }

    @Setter
    @Getter
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private long   expiresIn; // seconds
        // ctor + getters/setters
        public LoginResponse() {}
        public LoginResponse(String accessToken, String refreshToken, long expiresIn) {
            this.accessToken = accessToken; this.refreshToken = refreshToken; this.expiresIn = expiresIn;
        }

    }

    @Setter
    @Getter
    public static class RefreshRequest {
        private String refreshToken;

    }
}