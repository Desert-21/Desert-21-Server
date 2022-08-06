package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import lombok.Getter;

@Getter
public class UnitsTrainedNotification extends TurnResolutionNotificationBase {
    private Location location;
    private UnitType unitType;
    private int amount;

    public UnitsTrainedNotification(long millisecondsToView, Location location, UnitType unitType, int amount) {
        super(millisecondsToView);
        this.location = location;
        this.unitType = unitType;
        this.amount = amount;
    }
}
