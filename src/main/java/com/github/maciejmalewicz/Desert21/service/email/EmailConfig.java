package com.github.maciejmalewicz.Desert21.service.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.authentication.email")
@Data
@Component
public class EmailConfig {

    private String name;
    private String address;
    private String token;
}
