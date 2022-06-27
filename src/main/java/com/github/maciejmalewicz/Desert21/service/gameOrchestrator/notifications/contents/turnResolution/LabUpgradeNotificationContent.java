package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import lombok.Getter;

@Getter
public class LabUpgradeNotificationContent extends TurnResolutionNotificationBase {

    private LabUpgrade upgrade;
    private String playerId;

    public LabUpgradeNotificationContent(long millisecondsToView, LabUpgrade upgrade, String playerId) {
        super(millisecondsToView);
        this.upgrade = upgrade;
        this.playerId = playerId;
    }
}
