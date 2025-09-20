package org.rakhmonov.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.authservice.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .isDeleted(user.getIsDeleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
