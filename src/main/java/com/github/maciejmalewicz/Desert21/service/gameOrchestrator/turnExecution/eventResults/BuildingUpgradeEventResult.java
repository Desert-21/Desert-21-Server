package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.BuildingUpgradedNotification;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.BUILDING_UPGRADED_NOTIFICATION;

public record BuildingUpgradeEventResult(int fromLevel, int toLevel, Location location) implements EventResult {
    @Override
    public List<Notification<?>> forBoth() {
        var notification = new Notification<>(
                BUILDING_UPGRADED_NOTIFICATION,
                new BuildingUpgradedNotification(millisecondsToView(), fromLevel, toLevel, location)
        );
        return List.of(notification);
    }

    @Override
    public long millisecondsToView() {
        return 2000;
    }
}
