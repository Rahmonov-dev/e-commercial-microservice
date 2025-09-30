package org.rakhmonov.authservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rakhmonov.authservice.dto.request.LoginRequest;
import org.rakhmonov.authservice.dto.request.RegisterRequest;
import org.rakhmonov.authservice.entity.Permission;
import org.rakhmonov.authservice.entity.Role;
import org.rakhmonov.authservice.entity.User;
import org.rakhmonov.authservice.repo.PermissionRepository;
import org.rakhmonov.authservice.repo.RoleRepository;
import org.rakhmonov.authservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        // Create test permission
        testPermission = Permission.builder()
                .name("USER_READ")
                .description("Read user permission")
                .resource("USER")
                .action("READ")
                .build();
        testPermission = permissionRepository.save(testPermission);

        // Create test role
        testRole = Role.builder()
                .name("CUSTOMER")
                .description("Customer role")
                .permissions(List.of(testPermission))
                .build();
        testRole = roleRepository.save(testRole);
    }

    @Test
    void testRegisterUser_ShouldCreateUserSuccessfully() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
                "+998901234567",
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        // Verify user was created
        assertTrue(userRepository.findByPhoneNumber("+998901234567").isPresent());
    }

    @Test
    void testLoginUser_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Given - Create a user first
        User user = User.builder()
                .phoneNumber("+998901234567")
                .password(passwordEncoder.encode("password123"))
                .firstName("John")
                .lastName("Doe")
                .role(testRole)
                .build();
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("+998901234567", "password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void testLoginUser_WithInvalidCredentials_ShouldReturnError() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("+998901234567", "wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRegisterUser_WithExistingPhoneNumber_ShouldReturnError() throws Exception {
        // Given - Create existing user
        User existingUser = User.builder()
                .phoneNumber("+998901234567")
                .password(passwordEncoder.encode("password123"))
                .firstName("John")
                .lastName("Doe")
                .role(testRole)
                .build();
        userRepository.save(existingUser);

        RegisterRequest registerRequest = new RegisterRequest(
                "+998901234567",
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
