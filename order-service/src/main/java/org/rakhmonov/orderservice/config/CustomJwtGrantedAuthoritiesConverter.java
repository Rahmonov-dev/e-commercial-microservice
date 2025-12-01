package org.rakhmonov.orderservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        Object authoritiesClaim = jwt.getClaim("authorities");
        if (authoritiesClaim instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> authorityList = (List<String>) authoritiesClaim;
            List<GrantedAuthority> grantedAuthorities = authorityList.stream()
                    .map(authority -> {
                        if (!authority.startsWith("ROLE_")) {
                            return new SimpleGrantedAuthority("ROLE_" + authority);
                        }
                        return new SimpleGrantedAuthority(authority);
                    })
                    .collect(Collectors.toList());
            authorities.addAll(grantedAuthorities);
        }
        
        Object roleClaim = jwt.getClaim("role");
        if (roleClaim != null) {
            String role = roleClaim.toString();
            if (!role.startsWith("ROLE_")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            } else {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return authorities;
    }
}









