package com.github.maciejmalewicz.Desert21.misc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record UsersData (
        String id,
        String nickname
) {
}
