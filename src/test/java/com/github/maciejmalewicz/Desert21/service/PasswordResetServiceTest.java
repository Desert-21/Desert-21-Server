package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.PasswordResetLink;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.PasswordResetLinkRepository;
import com.github.maciejmalewicz.Desert21.service.email.PasswordResetEmailSender;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class PasswordResetServiceTest {

    private PasswordResetService tested;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private PasswordResetLinkRepository passwordResetLinkRepository;

    private PasswordResetEmailSender passwordResetEmailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        passwordResetEmailSender = mock(PasswordResetEmailSender.class);
        passwordEncoder = spy(passwordEncoder);
        doReturn("ENCODED").when(passwordEncoder).encode(anyString());
        tested = new PasswordResetService(userRepository, passwordResetLinkRepository, passwordResetEmailSender, passwordEncoder);

        userRepository.save(new ApplicationUser(
                "macior",
                new LoginData("macior@gmail.com", "Password"))
        );
    }

    @Test
    void requestPasswordResetEmailNotFound() {
        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.requestPasswordReset("unknownEmail@gmail.com");
        });
        assertEquals("Could not find the email!", exc.getMessage());
    }

    @Test
    void requestPasswordResetHappyPath() throws NotAcceptableException {
        tested.requestPasswordReset("macior@gmail.com");

        var passwordResetLink = passwordResetLinkRepository.findAll().stream().findFirst().orElseThrow();
        assertEquals("macior@gmail.com", passwordResetLink.getEmail());
        assertNotNull(passwordResetLink.getActivationCode());
        assertNotNull(passwordResetLink.getExpiryDate());

        var userId = userRepository.findFirstByEmail("macior@gmail.com").orElseThrow().getId();
        assertEquals(userId, passwordResetLink.getUserId());

        verify(passwordResetEmailSender, times(1)).sendPasswordResetEmail(
                passwordResetLink.getActivationCode(),
                passwordResetLink.getId(),
                "macior",
                "macior@gmail.com"
        );
    }

    @Test
    void executePasswordResetLinkNotFound() {
        var id = passwordResetLinkRepository.save(new PasswordResetLink(
                "macior@gmail.com",
                "code",
                new Date(),
                "userId"
        )).getId();
        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.executePasswordReset("macior@gmail.com", id + "dfhjdsfjdhsnf", "code", "Password2");
        });
        assertEquals("Password reset link not found!", exc.getMessage());
    }

    @Test
    void executePasswordResetWrongCode() {
        var id = passwordResetLinkRepository.save(new PasswordResetLink(
                "macior@gmail.com",
                "code",
                new Date(),
                "userId"
        )).getId();
        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.executePasswordReset("macior@gmail.com", id, "code2", "Password2");
        });
        assertEquals("Password reset link is invalid!", exc.getMessage());
    }

    @Test
    void executePasswordResetWrongEmail() {
        var id = passwordResetLinkRepository.save(new PasswordResetLink(
                "macior@gmail.com",
                "code",
                new Date(),
                "userId"
        )).getId();
        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.executePasswordReset("wrong@gmail.com", id, "code", "Password2");
        });
        assertEquals("Password reset link is invalid!", exc.getMessage());
    }

    @Test
    void executePasswordResetExpiredLink() {
        var id = passwordResetLinkRepository.save(new PasswordResetLink(
                "macior@gmail.com",
                "code",
                new Date(0L),
                "userId"
        )).getId();
        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.executePasswordReset("macior@gmail.com", id, "code", "Password2");
        });
        assertEquals("Password reset link has expired!", exc.getMessage());
    }

    @Test
    void executePasswordResetUserNotFound() {
        var id = passwordResetLinkRepository.save(new PasswordResetLink(
                "macior@gmail.com",
                "code",
                new Date(Long.MAX_VALUE),
                "userId"
        )).getId();
        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.executePasswordReset("macior@gmail.com", id, "code", "Password2");
        });
        assertEquals("User not found! Have you deleted your account?", exc.getMessage());
    }

    @Test
    void executePasswordResetHappyPath() throws NotAcceptableException {
        var userBefore = userRepository.findFirstByEmail("macior@gmail.com").orElseThrow();
        var passwordBefore = userBefore.getPassword();
        var userId = userBefore.getId();
        var id = passwordResetLinkRepository.save(new PasswordResetLink(
                "macior@gmail.com",
                "code",
                new Date(Long.MAX_VALUE),
                userId
        )).getId();
        tested.executePasswordReset("macior@gmail.com", id, "code", "Password2");

        var passwordAfter = userRepository.findFirstByEmail("macior@gmail.com").orElseThrow()
                .getPassword();

        assertNotEquals(passwordAfter, passwordBefore);
        assertEquals("ENCODED", passwordAfter);
    }
}