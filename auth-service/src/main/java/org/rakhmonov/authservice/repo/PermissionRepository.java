package org.rakhmonov.authservice.repo;

import org.rakhmonov.authservice.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    Optional<Permission> findByResourceAndAction(String resource, String action);
    List<Permission> findByResource(String resource);

    // Soft delete methods
    Optional<Permission> findByNameAndIsDeletedFalse(String name);
    List<Permission> findByResourceAndIsDeletedFalse(String resource);
    List<Permission> findByActionAndIsDeletedFalse(String action);
    List<Permission> findByIsDeletedFalse();
}
