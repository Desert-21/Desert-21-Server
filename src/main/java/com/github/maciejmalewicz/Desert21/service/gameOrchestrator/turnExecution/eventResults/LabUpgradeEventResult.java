package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.LabUpgradeNotificationContent;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.LAB_UPGRADE_NOTIFICATION;

public record LabUpgradeEventResult(LabUpgrade upgrade, String playerId) implements EventResult {

    @Override
    public long millisecondsToView() {
        return 2000;
    }

    @Override
    public List<Notification<?>> forBoth() {
        return List.of(
                new Notification<>(LAB_UPGRADE_NOTIFICATION,
                        new LabUpgradeNotificationContent(
                                millisecondsToView(),
                                upgrade,
                                playerId
                        )
                )
        );
    }
}
