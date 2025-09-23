package org.rakhmonov.authservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.LoginRequest;
import org.rakhmonov.authservice.dto.request.RefreshTokenRequest;
import org.rakhmonov.authservice.dto.request.RegisterRequest;
import org.rakhmonov.authservice.dto.response.AuthResponse;
import org.rakhmonov.authservice.dto.response.LoginResponse;
import org.rakhmonov.authservice.entity.User;
import org.rakhmonov.authservice.entity.Role;
import org.rakhmonov.authservice.repo.UserRepository;
import org.rakhmonov.authservice.repo.RoleRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<Long, String> redisTemplate;

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("User with this phone number already exists");
        }

        // Get default customer role
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default customer role not found"));

        User users = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(customerRole)
                .build();

        User savedUser = userRepository.save(users);
        return new LoginResponse(savedUser.getFirstName(), savedUser.getLastName(), savedUser.getPhoneNumber());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getPhoneNumber());
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber()).orElseThrow();

        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails, user.getId());

        redisTemplate.opsForValue().set(user.getId(), token, jwtService.getRefreshTokenExpiration(refreshToken));

        return new AuthResponse(token, refreshToken, "Login successful",
                user.getPhoneNumber(), user.getFirstName(), user.getLastName());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                jwtService.extractPhoneNumberFromRefreshToken(request.getRefreshToken()));
        if (!jwtService.isRefreshTokenValid(request.getRefreshToken())) {
            throw new RuntimeException("Refresh token invalid or expired");
        }
        String token = jwtService.generateToken(userDetails);
        User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new AuthResponse(token, request.getRefreshToken(), "Token refreshed",
                user.getPhoneNumber(), user.getFirstName(), user.getLastName());
    }

    public void logout(RefreshTokenRequest request) {
        if (request.getRefreshToken() != null) {
            Long userId = jwtService.extractUserIdFromRefreshToken(request.getRefreshToken());
            redisTemplate.delete(userId);
        }
    }
}
