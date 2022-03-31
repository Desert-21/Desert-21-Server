package com.github.maciejmalewicz.Desert21.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthoritiesUtilsTest {

    @Test
    void getIdAuthorityFromAuthoritiesHappyPath() {
        var authorities = List.of(
                new SimpleGrantedAuthority("SOME_RANDOM_AUTHORITY"),
                new SimpleGrantedAuthority("USER_ID1234")
        );
        var authorityOpt = AuthoritiesUtils.getIdAuthorityFromAuthorities(authorities);
        assertTrue(authorityOpt.isPresent());
        assertEquals(authorityOpt.get(), "USER_ID1234");
    }

    @Test
    void getIdAuthorityFromAuthoritiesUnhappyPath() {
        var authorities = List.of(
                new SimpleGrantedAuthority("SOME_RANDOM_AUTHORITY"),
                new SimpleGrantedAuthority("US_ER_ID1234")
        );
        var authorityOpt = AuthoritiesUtils.getIdAuthorityFromAuthorities(authorities);
        assertTrue(authorityOpt.isEmpty());
    }

    @Test
    void getIdFromAuthoritiesHappyPath() {
        var authorities = List.of(
                new SimpleGrantedAuthority("SOME_RANDOM_AUTHORITY"),
                new SimpleGrantedAuthority("USER_ID1234")
        );
        var authorityOpt = AuthoritiesUtils.getIdFromAuthorities(authorities);
        assertTrue(authorityOpt.isPresent());
        assertEquals(authorityOpt.get(), "ID1234");
    }

    @Test
    void getIdFromAuthoritiesUnhappyPath() {
        var authorities = List.of(
                new SimpleGrantedAuthority("SOME_RANDOM_AUTHORITY"),
                new SimpleGrantedAuthority("US_ER_ID1234")
        );
        var authorityOpt = AuthoritiesUtils.getIdFromAuthorities(authorities);
        assertTrue(authorityOpt.isEmpty());
    }
}