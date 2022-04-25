package com.github.maciejmalewicz.Desert21.models.turnExecution;

import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;

import java.util.List;

public record EventExecutionResult(TurnExecutionContext context, List<EventResult> results) {
}
