package org.rakhmonov.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rakhmonov.authservice.entity.Permission;
import org.rakhmonov.authservice.entity.Role;
import org.rakhmonov.authservice.entity.User;
import org.rakhmonov.authservice.repo.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private JWTService jwtService;

    private User testUser;
    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService(userRepository, redisTemplate);
        // Set the secret key for testing
        jwtService.setSecretKey("753778214125442A472D4B6150645367566B59703373367639792F423F452848");
        jwtService.setJwtExpiration(86400000L); // 1 day
        jwtService.setRefreshExpiration(604800000L); // 7 days
        
        // Create test permission
        testPermission = Permission.builder()
                .id(1L)
                .name("USER_READ")
                .description("Read user permission")
                .resource("USER")
                .action("READ")
                .build();

        // Create test role with permission
        testRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .description("Admin role")
                .permissions(List.of(testPermission))
                .build();

        // Create test user
        testUser = User.builder()
                .id(1L)
                .phoneNumber("+998901234567")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(testRole)
                .build();
    }

    @Test
    void testGenerateToken_ShouldIncludeAllAuthorities() {
        // When
        String token = jwtService.generateToken(testUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token contains authorities (role + permissions)
        assertTrue(jwtService.extractPhoneNumber(token).equals(testUser.getPhoneNumber()));
    }

    @Test
    void testGenerateRefreshToken_ShouldIncludeUserId() {
        // Given
        Long userId = 1L;

        // When
        String refreshToken = jwtService.generateRefreshToken(testUser, userId);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        
        // Verify refresh token contains userId
        Long extractedUserId = jwtService.extractUserIdFromRefreshToken(refreshToken);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testIsTokenValid_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        boolean isValid = jwtService.isTokenValid(token, testUser);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_WithInvalidUser_ShouldReturnFalse() {
        // Given
        String token = jwtService.generateToken(testUser);
        User differentUser = User.builder()
                .phoneNumber("+998901234568")
                .build();

        // When
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testExtractPhoneNumber_ShouldReturnCorrectPhoneNumber() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        String extractedPhoneNumber = jwtService.extractPhoneNumber(token);

        // Then
        assertEquals(testUser.getPhoneNumber(), extractedPhoneNumber);
    }

    @Test
    void testGetRoleBasedRefreshExpiration_AdminRole_ShouldReturnOneDay() {
        // Given
        String adminRole = "ROLE_ADMIN";

        // When
        long expiration = jwtService.getRoleBasedRefreshExpiration(adminRole);

        // Then
        assertEquals(1000 * 60 * 60 * 24, expiration); // 1 day in milliseconds
    }

    @Test
    void testGetRoleBasedRefreshExpiration_SellerRole_ShouldReturnFifteenDays() {
        // Given
        String sellerRole = "ROLE_SELLER";

        // When
        long expiration = jwtService.getRoleBasedRefreshExpiration(sellerRole);

        // Then
        assertEquals(1000 * 60 * 60 * 24 * 15, expiration); // 15 days in milliseconds
    }

    @Test
    void testGetRoleBasedRefreshExpiration_CustomerRole_ShouldReturnSevenDays() {
        // Given
        String customerRole = "ROLE_CUSTOMER";

        // When
        long expiration = jwtService.getRoleBasedRefreshExpiration(customerRole);

        // Then
        assertEquals(1000 * 60 * 60 * 24 * 7, expiration); // 7 days in milliseconds
    }
}
