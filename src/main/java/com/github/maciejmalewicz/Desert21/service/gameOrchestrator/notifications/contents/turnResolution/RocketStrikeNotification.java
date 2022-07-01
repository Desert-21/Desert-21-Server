package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import lombok.Getter;

@Getter
public class RocketStrikeNotification extends TurnResolutionNotificationBase {

    private Army defendersBefore;
    private Army defendersAfter;

    public RocketStrikeNotification(long millisecondsToView, Army defendersBefore, Army defendersAfter) {
        super(millisecondsToView);
        this.defendersBefore = defendersBefore;
        this.defendersAfter = defendersAfter;
    }
}
