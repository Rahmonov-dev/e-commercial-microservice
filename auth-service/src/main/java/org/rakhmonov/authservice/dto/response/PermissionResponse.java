package org.rakhmonov.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rakhmonov.authservice.entity.Permission;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {

    private Long id;
    private String name;
    private String description;
    private String resource;
    private String action;
    private Boolean isDeleted;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;

    public static PermissionResponse fromEntity(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .isDeleted(permission.getIsDeleted())
                .createdBy(permission.getCreatedBy())
                .build();
    }
}
