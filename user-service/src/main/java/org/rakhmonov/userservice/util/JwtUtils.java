package org.rakhmonov.userservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtUtils {
    
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            Object roleClaim = jwt.getClaim("role");
            if (roleClaim != null) {
                return roleClaim.toString();
            }
        }
        
        return null;
    }
    
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            Object userIdClaim = jwt.getClaim("userId");
            if (userIdClaim != null) {
                if (userIdClaim instanceof Long) {
                    return (Long) userIdClaim;
                } else if (userIdClaim instanceof Integer) {
                    return ((Integer) userIdClaim).longValue();
                } else if (userIdClaim instanceof String) {
                    try {
                        return Long.parseLong((String) userIdClaim);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        
        return null;
    }
    
    public static String getCurrentUserPhoneNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getSubject();
        }
        
        return null;
    }
}


