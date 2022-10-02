package com.github.maciejmalewicz.Desert21.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class AccountActivationEmailSender {

    private final static String accountActivationPath = "/activate-code/";
    private final static String messageTemplate = """
            Hi %s,
                        
            Dude from Desert 21 here! Your account is almost there.
            Click on the link below to activate your account and start
            enjoying probably the first non pay-to-win and not real time
            strategic game in the internet:
            %s
                        
            Enjoy,
            Dude from Desert 21
            """;

    private final EmailSendingService emailSendingService;

    @Value("${spring.addresses.baseUrl}")
    private String baseUrl;

    public AccountActivationEmailSender(EmailSendingService emailSendingService) {
        this.emailSendingService = emailSendingService;
    }

    public void sendActivationCode(String email, String nickname, String activationCode) throws MessagingException {
        var link = baseUrl + accountActivationPath + email + "/" + activationCode;
        var message = String.format(messageTemplate, nickname, link);
        emailSendingService.send("Desert 21 Account Activation", message, email);
    }

}
