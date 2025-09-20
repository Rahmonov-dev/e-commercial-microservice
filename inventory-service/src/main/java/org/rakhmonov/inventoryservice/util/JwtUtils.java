package org.rakhmonov.inventoryservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtils {
    
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            // Try to get role from JWT claims
            Object roleClaim = jwt.getClaim("role");
            if (roleClaim != null) {
                return roleClaim.toString();
            }
            
            // Fallback to authorities
            Collection<String> authorities = jwtAuth.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList());
            
            if (!authorities.isEmpty()) {
                return authorities.iterator().next();
            }
        }
        
        return null;
    }
    
    public static List<String> getAllUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList());
        }
        
        return List.of();
    }
    
    public static String getCurrentUserPhoneNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getSubject(); // Usually contains user ID or phone number
        }
        
        return null;
    }
    
    public static boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return role.equals(currentRole);
    }
    
    public static boolean hasAnyRole(String... roles) {
        List<String> userRoles = getAllUserRoles();
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
