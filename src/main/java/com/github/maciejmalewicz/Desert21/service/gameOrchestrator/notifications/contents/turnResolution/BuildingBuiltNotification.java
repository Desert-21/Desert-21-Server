package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Getter;

@Getter
public class BuildingBuiltNotification extends TurnResolutionNotificationBase {

    private Location location;
    private BuildingType buildingType;

    public BuildingBuiltNotification(long millisecondsToView, Location location, BuildingType buildingType) {
        super(millisecondsToView);
        this.location = location;
        this.buildingType = buildingType;
    }
}
