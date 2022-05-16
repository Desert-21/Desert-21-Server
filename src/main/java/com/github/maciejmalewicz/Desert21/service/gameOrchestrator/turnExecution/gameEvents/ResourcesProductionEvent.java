package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_THIS_TURN;

//dummy event - executor launched always on empty list
public class ResourcesProductionEvent extends GameEvent {

    public ResourcesProductionEvent() {
        super(END_OF_THIS_TURN);
    }
}
