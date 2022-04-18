package com.github.maciejmalewicz.Desert21.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.ActionType;

import java.util.LinkedHashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PlayersActionDto(ActionType type, LinkedHashMap<?, ?> content) {
}
