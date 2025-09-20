package org.rakhmonov.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.authservice.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;
    private Set<PermissionResponse> permissions;

    public static RoleResponse fromEntity(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .isDeleted(role.getIsDeleted())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .permissions(role.getPermissions() != null ? 
                    role.getPermissions().stream()
                        .map(PermissionResponse::fromEntity)
                        .collect(Collectors.toSet()) : null)
                .build();
    }
}
