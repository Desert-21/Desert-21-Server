package com.github.maciejmalewicz.Desert21.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record RegistrationDto (
        @NotBlank @Size(min = 2) @Size(max = 200) String nickname,
        @NotBlank @Size(min = 2) @Size(max = 200) String email,
        @NotBlank @Size(min = 8) @Size(max = 200) String password
) {
}
