package com.github.maciejmalewicz.Desert21.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailSendingService {

    @Autowired
    private EmailConfig config;

    public void send (String topic, String messageToSend, String email) throws MessagingException {
        String host = config.getHost();
        final String user = config.getUser();
        final String password = config.getPassword();

        //Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("user", user);
        props.put("password", password);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", config.getPort());
        props.put("mail.smtp.socketFactory.port", config.getPort());
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(topic);
        message.setText(messageToSend);

        Transport.send(message);
    }
}
