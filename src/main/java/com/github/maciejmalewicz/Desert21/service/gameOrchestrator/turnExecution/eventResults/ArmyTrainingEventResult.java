package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.UnitsTrainedNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.UNITS_TRAINED_NOTIFICATION;

public record ArmyTrainingEventResult(Location location, UnitType unitType, int amount) implements EventResult {
    @Override
    public long millisecondsToView() {
        return 2000;
    }

    @Override
    public List<Notification<?>> forCurrentPlayer() {
        return List.of(new Notification<>(
                UNITS_TRAINED_NOTIFICATION,
                new UnitsTrainedNotification(
                        millisecondsToView(),
                        location,
                        unitType,
                        amount
                )
        ));
    }
}
