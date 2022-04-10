package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.AccountAcceptanceRequest;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.dto.AccountActivationDto;
import com.github.maciejmalewicz.Desert21.dto.RegistrationDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.AccountAcceptanceRequestRepository;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.service.email.AccountActivationEmailSender;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class RegistrationServiceTest {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private AccountAcceptanceRequestRepository accountAcceptanceRequestRepository;

    private AccountActivationEmailSender accountActivationEmailSender;

    private RegistrationService tested;

    @BeforeEach
    void setupTested() {
        var passwordEncoder = mock(PasswordEncoder.class);
        doReturn("PASSWORD_ENCODED").when(passwordEncoder).encode(any(CharSequence.class));

        accountActivationEmailSender = mock(AccountActivationEmailSender.class);

        tested = new RegistrationService(
                applicationUserRepository,
                accountAcceptanceRequestRepository,
                passwordEncoder,
                accountActivationEmailSender
        );
    }

    void setupActivationCode() {
        accountAcceptanceRequestRepository.save(new AccountAcceptanceRequest(
                "m@gmail.com",
                "macior123456",
                "PASSWORD",
                "ACTIVATION_CODE"
        ));
    }

    @Test
    void registerUser() throws Exception {
        tested.registerUser(new RegistrationDto(
                "macior123456",
                "m@gmail.com",
                "Any password"
        ));

        var acceptanceRequest = accountAcceptanceRequestRepository.findAll().stream()
                .findAny()
                .orElseThrow();
        assertNotNull(acceptanceRequest.getActivationCode());
        assertEquals("macior123456", acceptanceRequest.getNickname());
        assertEquals("m@gmail.com", acceptanceRequest.getEmail());
        assertEquals("PASSWORD_ENCODED", acceptanceRequest.getPassword());

        verify(accountActivationEmailSender, times(1))
                .sendActivationCode(eq("m@gmail.com"), eq("macior123456"), anyString());
    }

    @Test
    void registerUserCouldNotSendAnEmail() throws Exception {
        doThrow(new MessagingException("EMAIL EXCEPTION"))
                .when(accountActivationEmailSender)
                .sendActivationCode(anyString(), anyString(), anyString());

        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.registerUser(new RegistrationDto(
                    "macior123456",
                    "m@gmail.com",
                    "Any password"
            ));
        });
        assertEquals("Could not send an email to address: m@gmail.com", exception.getMessage());
        assertEquals(0, accountAcceptanceRequestRepository.findAll().size());
    }

    @Test
    void registerUserNicknameOccupiedInApplicationUser() throws Exception {
        applicationUserRepository.save(new ApplicationUser(
                "macior123456",
                new LoginData("macior@gmail.com", "PASSWORD")
        ));

        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.registerUser(new RegistrationDto(
                    "macior123456",
                    "m@gmail.com",
                    "Any password"
            ));
        });
        assertEquals("Nickname already occupied!", exception.getMessage());
        assertEquals(0, accountAcceptanceRequestRepository.findAll().size());
        verify(accountActivationEmailSender, never())
                .sendActivationCode(anyString(), anyString(), anyString());
    }

    @Test
    void registerUserNicknameOccupiedInActivationCode() throws Exception {
        accountAcceptanceRequestRepository.save(new AccountAcceptanceRequest(
                "macior@gmail.com",
                "macior123456",
                "PASSWORD",
                "ACTIVATION_CODE"
        ));

        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.registerUser(new RegistrationDto(
                    "macior123456",
                    "m@gmail.com",
                    "Any password"
            ));
        });
        assertEquals("Nickname already occupied!", exception.getMessage());
        assertEquals(1, accountAcceptanceRequestRepository.findAll().size());
        verify(accountActivationEmailSender, never())
                .sendActivationCode(anyString(), anyString(), anyString());
    }

    @Test
    void registerUserEmailOccupiedInApplicationUser() throws Exception {
        applicationUserRepository.save(new ApplicationUser(
                "macior12345678",
                new LoginData("m@gmail.com", "PASSWORD")
        ));

        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.registerUser(new RegistrationDto(
                    "macior123456",
                    "m@gmail.com",
                    "Any password"
            ));
        });
        assertEquals("Email already occupied!", exception.getMessage());
        assertEquals(0, accountAcceptanceRequestRepository.findAll().size());
        verify(accountActivationEmailSender, never())
                .sendActivationCode(anyString(), anyString(), anyString());
    }

    @Test
    void registerUserEmailOccupiedInActivationCode() throws Exception {
        accountAcceptanceRequestRepository.save(new AccountAcceptanceRequest(
                "m@gmail.com",
                "macior12345678",
                "PASSWORD",
                "ACTIVATION_CODE"
        ));

        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.registerUser(new RegistrationDto(
                    "macior123456",
                    "m@gmail.com",
                    "Any password"
            ));
        });
        assertEquals("Email already occupied!", exception.getMessage());
        assertEquals(1, accountAcceptanceRequestRepository.findAll().size());
        verify(accountActivationEmailSender, never())
                .sendActivationCode(anyString(), anyString(), anyString());
    }

    @Test
    void activateAccountHappyPath() throws Exception {
        setupActivationCode();
        tested.activateAccount(new AccountActivationDto(
                "m@gmail.com",
                "ACTIVATION_CODE"
        ));
        assertEquals(0, accountAcceptanceRequestRepository.findAll().size());
        var user = applicationUserRepository.findAll().stream()
                .findAny()
                .orElseThrow();
        assertEquals("macior123456", user.getNickname());
        assertEquals("m@gmail.com", user.getLoginData().getEmail());
        assertEquals("PASSWORD", user.getLoginData().getPassword());
    }

    @Test
    void activateAccountEmailNotFound() {
        setupActivationCode();
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.activateAccount(new AccountActivationDto(
                    "macior@gmail.com",
                    "ACTIVATION_CODE"
            ));
        });
        assertEquals("Account creation request with given email not found!", exception.getMessage());
        assertEquals(1, accountAcceptanceRequestRepository.findAll().size());
        assertEquals(0, applicationUserRepository.findAll().size());
    }

    @Test
    void activateAccountWringActivationCode() {
        setupActivationCode();
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.activateAccount(new AccountActivationDto(
                    "m@gmail.com",
                    "ACTIVATION_CODE_NOT_MATCHING"
            ));
        });
        assertEquals("Activation codes didn't match!", exception.getMessage());
        assertEquals(1, accountAcceptanceRequestRepository.findAll().size());
        assertEquals(0, applicationUserRepository.findAll().size());
    }
}