package com.github.maciejmalewicz.Desert21.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PlayersActionDto(String type, Object content) {
}
