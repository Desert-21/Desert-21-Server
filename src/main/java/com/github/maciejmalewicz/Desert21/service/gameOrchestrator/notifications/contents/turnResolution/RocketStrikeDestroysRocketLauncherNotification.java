package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Getter;

@Getter
public class RocketStrikeDestroysRocketLauncherNotification extends TurnResolutionNotificationBase {
    private Location location;

    public RocketStrikeDestroysRocketLauncherNotification(long millisecondsToView, Location location) {
        super(millisecondsToView);
        this.location = location;
    }
}
