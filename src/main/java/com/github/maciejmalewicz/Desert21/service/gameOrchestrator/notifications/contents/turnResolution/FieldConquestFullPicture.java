package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.FightingArmy;
import lombok.Getter;

@Getter
public class FieldConquestFullPicture extends TurnResolutionNotificationBase{
    private Location location;
    private FightingArmy attackersBefore;
    private FightingArmy defendersBefore;
    private FightingArmy attackersAfter;
    private FightingArmy defendersAfter;

    public FieldConquestFullPicture(
            long millisecondsToView,
            Location location,
            FightingArmy attackersBefore,
            FightingArmy defendersBefore,
            FightingArmy attackersAfter,
            FightingArmy defendersAfter) {
        super(millisecondsToView);
        this.location = location;
        this.attackersBefore = attackersBefore;
        this.defendersBefore = defendersBefore;
        this.attackersAfter = attackersAfter;
        this.defendersAfter = defendersAfter;
    }
}
