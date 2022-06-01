package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonFormat(shape= JsonFormat.Shape.STRING)
public enum UnitType {
    DROID,
    TANK,
    CANNON
}
