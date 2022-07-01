package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Getter;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_THIS_TURN;

@Getter
public class RocketStrikeEvent extends GameEvent {

    private Location location;
    private boolean isAttackingRocket;

    public RocketStrikeEvent(Location location, boolean isAttackingRocket) {
        super(END_OF_THIS_TURN);
        this.location = location;
        this.isAttackingRocket = isAttackingRocket;
    }
}
