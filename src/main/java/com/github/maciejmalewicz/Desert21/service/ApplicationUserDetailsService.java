package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository repository;

    public ApplicationUserDetailsService(ApplicationUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findFirstByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }
}
