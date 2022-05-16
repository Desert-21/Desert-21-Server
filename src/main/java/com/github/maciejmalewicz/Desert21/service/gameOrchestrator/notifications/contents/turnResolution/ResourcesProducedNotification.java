package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import lombok.Getter;

@Getter
public class ResourcesProducedNotification extends TurnResolutionNotificationBase {

    private ResourceSet produced;
    private String playerId;

    public ResourcesProducedNotification(long millisecondsToView, ResourceSet resourceSet, String playerId) {
        super(millisecondsToView);
        this.produced = resourceSet;
        this.playerId = playerId;
    }
}
