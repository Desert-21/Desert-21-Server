package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.FightingArmy;
import lombok.Getter;

@Getter
public class FieldConquestAttackerOnly extends TurnResolutionNotificationBase {

    private Location location;
    private FightingArmy attackersBefore;
    private FightingArmy attackersAfter;

    public FieldConquestAttackerOnly(
            long millisecondsToView,
            Location location,
            FightingArmy attackersBefore,
            FightingArmy attackersAfter) {
        super(millisecondsToView);
        this.location = location;
        this.attackersBefore = attackersBefore;
        this.attackersAfter = attackersAfter;
    }
}
