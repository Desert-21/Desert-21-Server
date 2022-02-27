package com.github.maciejmalewicz.Desert21.utils;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.USER_ID_AUTH_PREFIX;

public class AuthoritiesUtils {

    public static Optional<String> getIdAuthorityFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s.startsWith(USER_ID_AUTH_PREFIX))
                .findFirst();
    }

    public static Optional<String> getIdFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return getIdAuthorityFromAuthorities(authorities).map(a -> a.replace(USER_ID_AUTH_PREFIX, ""));
    }
}
