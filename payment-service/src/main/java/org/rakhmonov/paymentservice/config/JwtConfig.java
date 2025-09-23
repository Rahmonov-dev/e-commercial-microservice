package org.rakhmonov.paymentservice.config;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}


