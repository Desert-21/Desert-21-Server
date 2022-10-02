package com.github.maciejmalewicz.Desert21.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "spring.authentication.cors")
public class CorsConfig {

    private List<String> origins;
}
