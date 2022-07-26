package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Getter;

@Getter
public class RocketStrikeNotification extends TurnResolutionNotificationBase {

    private Location location;
    private Army defendersBefore;
    private Army defendersAfter;

    public RocketStrikeNotification(long millisecondsToView, Location location, Army defendersBefore, Army defendersAfter) {
        super(millisecondsToView);
        this.location = location;
        this.defendersBefore = defendersBefore;
        this.defendersAfter = defendersAfter;
    }
}
