package com.github.maciejmalewicz.Desert21.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PlayersTurnDto(String gameId, List<PlayersActionDto> actions) {
}
