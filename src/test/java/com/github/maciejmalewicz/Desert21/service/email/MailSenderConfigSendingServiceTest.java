package com.github.maciejmalewicz.Desert21.service.email;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.emails.Emails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailSenderConfigSendingServiceTest {

    private EmailConfig emailConfig;
    private MailerSend mailerSend;

    private String savedToken;
    private Emails emails;

    @BeforeEach
    public void setup() {
        emailConfig = new EmailConfig();
        emailConfig.setName("Name");
        emailConfig.setAddress("service@email.com");
        emailConfig.setToken("TOKEN");

        mailerSend = mock(MailerSend.class);
        doAnswer(args -> { savedToken = args.getArgument(0, String.class); return null; })
                .when(mailerSend).setToken(anyString());

        emails = mock(Emails.class);
        doReturn(emails).when(mailerSend).emails();
    }

    @Test
    void send() throws Exception {
        var service = new EmailSendingService(emailConfig, mailerSend);

        assertEquals("TOKEN", savedToken);
        service.send("Topic", "Message", "Address");

        ArgumentCaptor<Email> emailArgumentCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emails, times(1)).send(emailArgumentCaptor.capture());

        var sentEmail = emailArgumentCaptor.getAllValues().stream()
                .findAny()
                .orElseThrow();

        assertEquals("Topic", sentEmail.subject);
        assertEquals("Message", sentEmail.text);

        assertEquals(1, sentEmail.recipients.size());
        assertEquals("Player", sentEmail.recipients.get(0).name);
        assertEquals("Address", sentEmail.recipients.get(0).email);

        assertEquals("Name", sentEmail.from.name);
        assertEquals("service@email.com", sentEmail.from.email);
    }
}