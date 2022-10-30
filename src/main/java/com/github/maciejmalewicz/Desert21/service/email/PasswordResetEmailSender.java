package com.github.maciejmalewicz.Desert21.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetEmailSender {

    @Value("${spring.addresses.baseUrl}")
    private String baseUrl;

    private final static String passwordResetPath = "/reset-password/";
    private final static String messageTemplate = """
            Hi %s,
                        
            Dude from Desert 21 here! You have requested a password reset, so here's the link:
            %s
            This link will be active only for 5 minutes, so be quick!
                  
            Thanks,
            Dude from Desert 21
            """;

    private final EmailSendingService emailSendingService;

    public PasswordResetEmailSender(EmailSendingService emailSendingService) {
        this.emailSendingService = emailSendingService;
    }

    public void sendPasswordResetEmail(String code, String id, String nickname, String email) {
        var link = baseUrl + passwordResetPath + id + "/" + code + "/" + email;
        var message = String.format(messageTemplate, nickname, link);
        emailSendingService.send("Desert 21 password reset", message, email);
    }
}
