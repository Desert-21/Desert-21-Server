package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.FieldConquestFullPicture;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.FieldConquestNoInfo;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.UNOCCUPIED_FIELD_ENEMY_CONQUEST_FAILED_NOTIFICATION;
import static com.github.maciejmalewicz.Desert21.config.Constants.UNOCCUPIED_FIELD_PLAYERS_CONQUEST_FAILED_NOTIFICATION;

public record UnoccupiedFieldConquestFailed(Location location, BattleResult battleResult) implements EventResult {

    @Override
    public long millisecondsToView() {
        return 3000;
    }

    @Override
    public List<Notification<?>> forCurrentPlayer() {
        var notification = new Notification<>(UNOCCUPIED_FIELD_PLAYERS_CONQUEST_FAILED_NOTIFICATION,
                new FieldConquestFullPicture(
                        millisecondsToView(),
                        location,
                        battleResult.attackersBefore(),
                        battleResult.defendersBefore(),
                        battleResult.attackersAfter(),
                        battleResult.defendersAfter()
                ));
        return List.of(notification);
    }

    @Override
    public List<Notification<?>> forOpponent() {
        var notification = new Notification<>(UNOCCUPIED_FIELD_ENEMY_CONQUEST_FAILED_NOTIFICATION,
                new FieldConquestNoInfo(
                        millisecondsToView(),
                        location
                ));
        return List.of(notification);
    }
}
