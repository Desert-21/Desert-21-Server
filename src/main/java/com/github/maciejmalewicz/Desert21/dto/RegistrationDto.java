package com.github.maciejmalewicz.Desert21.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record RegistrationDto (
        String nickname,
        String email,
        String password
) {
}
