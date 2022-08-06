package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArmyTrainingEvent extends GameEvent implements LocatableEvent {

    private Location location;
    private UnitType unitType;
    private int amount;

    public ArmyTrainingEvent(int turnsToExecute, Location location, UnitType unitType, int amount) {
        super(turnsToExecute);
        this.location = location;
        this.unitType = unitType;
        this.amount = amount;
    }
}
