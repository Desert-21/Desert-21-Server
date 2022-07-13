package com.github.maciejmalewicz.Desert21.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.ActionType;
import lombok.NonNull;

import java.util.LinkedHashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PlayersActionDto(@NonNull ActionType type, @NonNull LinkedHashMap<?, ?> content) {
}
