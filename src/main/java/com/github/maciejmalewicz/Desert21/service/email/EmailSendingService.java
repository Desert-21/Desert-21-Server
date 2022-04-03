package com.github.maciejmalewicz.Desert21.service.email;

import com.mongodb.internal.VisibleForTesting;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailSendingService {

    private final EmailConfig config;

    public EmailSendingService(EmailConfig config) {
        this.config = config;
    }

    public void send (String topic, String messageToSend, String email) throws MessagingException {
        var host = config.getHost();
        var user = config.getUser();
        var password = config.getPassword();
        var port = config.getPort();

        var session = buildSession(host, user, password, port);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(topic);
        message.setText(messageToSend);

        transportMessage(message);
    }

    private Session buildSession(String host, String user, String password, String port) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("user", user);
        props.put("password", password);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
    }

    @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
    void transportMessage(MimeMessage message) throws MessagingException {
        Transport.send(message);
    }


}
