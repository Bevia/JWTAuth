package org.corebaseit.jwtauth.config;

import org.corebaseit.jwtauth.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtBeans {

    @Bean
    public JwtService jwtService(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.accessMinutes}") long accessMinutes,
            @Value("${app.jwt.refreshDays}") long refreshDays
    ) {
        return new JwtService(issuer, secret, accessMinutes, refreshDays);
    }
}