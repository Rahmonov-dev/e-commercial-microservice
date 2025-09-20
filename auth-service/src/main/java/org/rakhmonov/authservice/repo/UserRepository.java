package org.rakhmonov.authservice.repo;

import org.rakhmonov.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    
    // Soft delete methods
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    List<User> findByIsDeletedFalse();
    Optional<User> findByPhoneNumberAndIsDeletedFalse(String phoneNumber);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    boolean existsByPhoneNumberAndIsDeletedFalse(String phoneNumber);
    boolean existsByEmailAndIsDeletedFalse(String email);
    
    // Find by role
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.isDeleted = false")
    List<User> findByRoleNameAndIsDeletedFalse(@Param("roleName") String roleName);
    
    // Find by status
    List<User> findByStatusAndIsDeletedFalse(User.UserStatus status);
    
    // Find by role and status
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.status = :status AND u.isDeleted = false")
    List<User> findByRoleNameAndStatusAndIsDeletedFalse(@Param("roleName") String roleName, @Param("status") User.UserStatus status);
}
