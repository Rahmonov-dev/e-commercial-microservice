package org.rakhmonov.authservice.repo;

import org.rakhmonov.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    
    Optional<Role> findByNameAndIsDeletedFalse(String name);
    
    Optional<Role> findByIdAndIsDeletedFalse(Long id);
    
    List<Role> findByIsDeletedFalse();
    
    @Query("SELECT r FROM Role r WHERE r.isDeleted = false AND r.name IN :names")
    List<Role> findByNamesAndIsDeletedFalse(@Param("names") List<String> names);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIsDeletedFalse(String name);
}
