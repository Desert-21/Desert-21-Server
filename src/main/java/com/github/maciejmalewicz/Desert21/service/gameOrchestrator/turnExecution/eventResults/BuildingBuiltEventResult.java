package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.BuildingBuiltNotification;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.BUILDING_BUILT_NOTIFICATION;

public record BuildingBuiltEventResult(Location location, BuildingType buildingType) implements EventResult {

    @Override
    public long millisecondsToView() {
        return 2000;
    }

    @Override
    public List<Notification<?>> forBoth() {
        return List.of(new Notification<>(
                BUILDING_BUILT_NOTIFICATION,
                new BuildingBuiltNotification(millisecondsToView(), location, buildingType)
        ));
    }
}
