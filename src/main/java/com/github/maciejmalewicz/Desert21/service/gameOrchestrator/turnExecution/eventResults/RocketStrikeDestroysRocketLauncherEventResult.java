package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.RocketStrikeDestroysRocketLauncherNotification;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.ROCKET_STRIKE_DESTROYS_ROCKET_LAUNCHER_NOTIFICATION;

public record RocketStrikeDestroysRocketLauncherEventResult(Location location) implements EventResult{
    @Override
    public long millisecondsToView() {
        return 3000;
    }

    @Override
    public List<Notification<?>> forBoth() {
        return List.of(
                new Notification<>(
                        ROCKET_STRIKE_DESTROYS_ROCKET_LAUNCHER_NOTIFICATION,
                        new RocketStrikeDestroysRocketLauncherNotification(
                                millisecondsToView(),
                                location
                        )
                )
        );
    }
}
