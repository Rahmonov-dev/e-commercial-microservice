package org.rakhmonov.authservice.service;

import lombok.RequiredArgsConstructor;
import org.rakhmonov.authservice.dto.request.UserUpdateRequest;
import org.rakhmonov.authservice.dto.response.UserResponse;
import org.rakhmonov.authservice.entity.User;
import org.rakhmonov.authservice.entity.Role;
import org.rakhmonov.authservice.repo.UserRepository;
import org.rakhmonov.authservice.repo.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumberAndIsDeletedFalse(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone number: " + phoneNumber));
        return UserResponse.fromEntity(user);
    }

    public UserResponse updateUser(UserUpdateRequest request, String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone number: " + phoneNumber));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public UserResponse updateUserRole(Long userId, Long roleId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Role role = roleRepository.findByIdAndIsDeletedFalse(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        user.setRole(role);
        
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsDeleted(true);
        
        userRepository.save(user);
    }

    public UserResponse activateUser(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setStatus(User.UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setStatus(User.UserStatus.INACTIVE);
        
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse changeUserStatus(Long id, User.UserStatus status) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setStatus(status);
        
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public List<UserResponse> getAllActiveUsers() {
        return userRepository.findByIsDeletedFalse().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(String roleName) {
        return userRepository.findByRoleNameAndIsDeletedFalse(roleName).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatusAndIsDeletedFalse(status).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
