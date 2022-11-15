package com.github.maciejmalewicz.Desert21.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetEmailSenderTest {

    private PasswordResetEmailSender tested;

    private EmailSendingService emailSendingService;

    @BeforeEach
    void setup() {
        emailSendingService = mock(EmailSendingService.class);
        tested = new PasswordResetEmailSender(emailSendingService);
    }

    private final String message = """
           Hi macior123456,
                                        
           Desert 21 Bot here! You have requested a password reset, so here's the link:
           http://www.desert21.com/reset-password/ID/ABC/macior@gmail.com
           This link will be active only for 5 minutes, so be quick!
                                        
           Thanks,
           Desert 21 Bot
            """;

    @Test
    void sendPasswordResetEmail() {
        ReflectionTestUtils.setField(tested, "baseUrl", "http://www.desert21.com");
        tested.sendPasswordResetEmail("ABC", "ID", "macior123456", "macior@gmail.com");

        verify(emailSendingService, times(1)).send("Desert 21 password reset", message, "macior@gmail.com");
    }
}