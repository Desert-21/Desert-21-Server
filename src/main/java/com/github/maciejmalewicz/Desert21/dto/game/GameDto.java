package com.github.maciejmalewicz.Desert21.dto.game;

import java.util.List;

public record GameDto(
        String gameId,
        List<PlayerDto> players,
        FieldDto[][] fields,
        StateManagerDto stateManager,
        List<EventDto> events
) {
}
