package com.github.maciejmalewicz.Desert21.config;

import com.mailersend.sdk.MailerSend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailSenderConfig {

    @Bean
    public MailerSend mailerSend() {
        return new MailerSend();
    }
}
