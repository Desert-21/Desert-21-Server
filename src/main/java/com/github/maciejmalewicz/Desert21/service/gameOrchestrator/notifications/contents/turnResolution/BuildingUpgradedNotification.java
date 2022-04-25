package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Getter;

@Getter
public class BuildingUpgradedNotification extends TurnResolutionNotificationBase {
    private int fromLevel;
    private int toLevel;
    private Location location;

    public BuildingUpgradedNotification(long millisecondsToView, int fromLevel, int toLevel, Location location) {
        super(millisecondsToView);
        this.fromLevel = fromLevel;
        this.toLevel = toLevel;
        this.location = location;
    }
}
