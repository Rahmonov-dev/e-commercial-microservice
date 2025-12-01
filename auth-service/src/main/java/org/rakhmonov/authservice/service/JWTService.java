package org.rakhmonov.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.entity.User;
import org.rakhmonov.authservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setJwtExpiration(long jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }

    @Value("${jwt.expiration}")
    private long jwtExpiration; // Access token uchun umumiy

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // Default refresh muddati (masalan 7 kun)

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // Access token
    public String generateToken(UserDetails userDetails) {
        HashMap<String, Object> claims = new HashMap<>();
        
        // Get all authorities (roles + permissions)
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        
        claims.put("authorities", authorities);
        
        // Get userId from userDetails (User entity implements UserDetails)
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            claims.put("userId", user.getId());
        } else {
            // Fallback: get userId from phoneNumber
            User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElse(null);
            if (user != null) {
                claims.put("userId", user.getId());
            }
        }
        
        return generateToken(claims, userDetails);
    }

    // Role asosida refresh token
    public String generateRefreshToken(UserDetails userDetails, Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        long roleBasedExpiration = getRoleBasedRefreshExpiration(
                userDetails.getAuthorities().iterator().next().getAuthority()
        );

        return buildToken(claims, userDetails, roleBasedExpiration);
    }

    public Duration getRefreshTokenExpiration(String token) {
        return Duration.ofMillis(extractExpiration(token).getTime() - System.currentTimeMillis());
    }

    private String generateToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isRefreshTokenValid(String token) {
        final Long userId =extractUserIdFromRefreshToken(token);
        String storedToken= (String) redisTemplate.opsForValue().get(userId);
        return storedToken!=null;
    }

    public String extractPhoneNumberFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserIdFromRefreshToken(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Rolega qarab refresh expiry
    public long getRoleBasedRefreshExpiration(String role) {
        switch (role) {
            case "ROLE_ADMIN":
                return 1000 * 60 * 60 * 24; // 1 kun
            case "ROLE_SELLER":
                return 1000 * 60 * 60 * 24 * 15; // 15 kun
            case "ROLE_CUSTOMER":
                return 1000 * 60 * 60 * 24 * 7; // 7 kun
            default:
                return refreshExpiration; // configdagi default qiymat
        }
    }
}
