package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class ApplicationUserDetailsServiceTest {

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private ApplicationUserDetailsService tested;

    @Test
    void loadUserByUsernameUserExists() {
        var user = new ApplicationUser(
                "macior123456",
                new LoginData("m@gmail.com", "Password1")
        );
        userRepository.save(user);

        var retrieved = tested.loadUserByUsername("m@gmail.com");
        assertEquals(user, retrieved);
    }

    @Test
    void loadUserByUsernameUserDoesNotExist() {
        var exception = assertThrows(UsernameNotFoundException.class, () -> {
            tested.loadUserByUsername("m@gmail.com");
        });
        assertEquals("Username not found!", exception.getMessage());
    }
}