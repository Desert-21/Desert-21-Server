package com.github.maciejmalewicz.Desert21.dto.game;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EventDto(String type, Object content) {
}
