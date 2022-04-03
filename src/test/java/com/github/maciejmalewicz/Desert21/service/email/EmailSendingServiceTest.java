package com.github.maciejmalewicz.Desert21.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

class EmailSendingServiceTest {

    private EmailConfig emailConfig;

    @BeforeEach
    public void setup() {
        emailConfig = new EmailConfig();
        emailConfig.setHost("smtp.gmail.com");
        emailConfig.setPassword("Password123");
        emailConfig.setUser("d21gsd21gs@gmail.com");
        emailConfig.setPort("587");
    }

    @Test
    void send() throws Exception {
        var service = new EmailSendingService(emailConfig);
        var tested = Mockito.spy(service);
        var argument = ArgumentCaptor.forClass(MimeMessage.class);
        doNothing().when(tested).transportMessage(argument.capture());

        tested.send("Example topic", "Hello message", "player@gmail.com");

        var allValues = argument.getAllValues();
        assertEquals(1, allValues.size());
        var messageParameter = allValues.get(0);

        //validate email
        var recipients = messageParameter.getAllRecipients();
        var receiver = recipients[0].toString();
        var subject = messageParameter.getSubject();
        var message = messageParameter.getContent();
        assertEquals("player@gmail.com", receiver);
        assertEquals("Example topic", subject);
        assertEquals("Hello message", message);

        //validate session
        var session = messageParameter.getSession();
        assertEquals("Password123", session.getProperty("password"));
        assertEquals("587", session.getProperty("mail.smtp.port"));
        assertEquals("d21gsd21gs@gmail.com", session.getProperty("user"));
        assertEquals("true", session.getProperty("mail.smtp.auth"));
        assertEquals("true", session.getProperty("mail.smtp.starttls.enable"));
        assertEquals("smtp.gmail.com", session.getProperty("mail.smtp.host"));
        assertEquals("587", session.getProperty("mail.smtp.socketFactory.port"));
    }
}