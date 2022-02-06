package com.github.maciejmalewicz.Desert21.config.security.jwt;

import com.google.common.net.HttpHeaders;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "application.jwt")
@Data
@Component
public class JwtConfig {

    private String secretKey;
    private String prefix;
    private int tokenExpirationAfterDays;

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }
}
