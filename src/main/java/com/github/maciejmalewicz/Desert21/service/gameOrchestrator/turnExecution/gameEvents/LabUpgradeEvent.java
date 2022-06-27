package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_THIS_TURN;

@NoArgsConstructor
@Getter
public class LabUpgradeEvent extends GameEvent {
    private LabUpgrade upgrade;

    public LabUpgradeEvent(LabUpgrade upgrade) {
        super(END_OF_THIS_TURN);
        this.upgrade = upgrade;
    }
}
