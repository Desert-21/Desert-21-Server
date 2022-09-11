package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class UsersServiceTest {

    @Autowired
    private UsersService tested;

    @Autowired
    private ApplicationUserRepository repository;

    @Test
    void getUsersDataHappyPath() throws AuthorizationException {
        var user = new ApplicationUser(
                "macior123456",
                new LoginData("m@gmail.com", "Password1")
        );
        user = repository.save(user);
        var retrieved = tested.getUsersData("m@gmail.com");
        assertEquals(user.getId(), retrieved.id());
        assertEquals("macior123456", retrieved.nickname());
    }

    @Test
    void getUsersDataNotFound() {
        var exception = assertThrows(AuthorizationException.class, () -> {
            tested.getUsersData("m@gmail.com");
        });
        assertEquals("User not found!", exception.getMessage());
    }
}