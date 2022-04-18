package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_THIS_TURN;

@NoArgsConstructor
@Getter
public class PaymentEvent extends GameEvent {
    private ResourceSet resourceSet;

    public PaymentEvent(ResourceSet resourceSet) {
        this.turnsToExecute = END_OF_THIS_TURN;
        this.resourceSet = resourceSet;
    }
}
