package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.ResourcesProducedNotification;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.RESOURCES_PRODUCED_NOTIFICATION;

public record ResourcesProducedResult(ResourceSet resourceSet, String playerId) implements EventResult {

    @Override
    public long millisecondsToView() {
        return 1000;
    }

    @Override
    public List<Notification<?>> forBoth() {
        var notification = new Notification<>(
                RESOURCES_PRODUCED_NOTIFICATION,
                new ResourcesProducedNotification(
                        millisecondsToView(),
                        resourceSet,
                        playerId
                )
        );
        return List.of(notification);
    }
}
