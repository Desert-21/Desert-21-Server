package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import lombok.Getter;

@Getter
public abstract class TurnResolutionNotificationBase {

    private final long millisecondsToView;

    protected TurnResolutionNotificationBase(long millisecondsToView) {
        this.millisecondsToView = millisecondsToView;
    }
}
