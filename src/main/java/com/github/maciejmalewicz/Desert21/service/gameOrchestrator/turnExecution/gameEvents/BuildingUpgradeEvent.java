package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_THIS_TURN;

@NoArgsConstructor
@Getter
public class BuildingUpgradeEvent extends GameEvent {

    private Location location;

    public BuildingUpgradeEvent(Location location) {
        this.turnsToExecute = END_OF_THIS_TURN;
        this.location = location;
    }
}
