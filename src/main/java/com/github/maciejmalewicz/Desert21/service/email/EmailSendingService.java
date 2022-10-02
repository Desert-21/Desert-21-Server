package com.github.maciejmalewicz.Desert21.service.email;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailSendingService {

    private final EmailConfig config;
    private final MailerSend mailerSend;

    public EmailSendingService(EmailConfig config, MailerSend mailerSend) {
        this.config = config;
        this.mailerSend = mailerSend;
        this.mailerSend.setToken(config.getToken());
    }

    public void send (String topic, String messageToSend, String emailAddress) throws MessagingException {
        Email email = new Email();

        email.setFrom(config.getName(), config.getAddress());
        email.addRecipient("Player", emailAddress);
        email.setSubject(topic);
        email.setPlain(messageToSend);

        try {
            mailerSend.emails().send(email);
        } catch (MailerSendException e) {
            throw new MessagingException("Could not send an email to: " + emailAddress);
        }
    }
}
