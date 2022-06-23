package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.FieldConquestFullPicture;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.ENEMY_CONQUEST_SUCCEEDED_NOTIFICATION;
import static com.github.maciejmalewicz.Desert21.config.Constants.PLAYERS_CONQUEST_SUCCEEDED_NOTIFICATION;

public record EnemyFieldConquestSucceeded(Location location, BattleResult battleResult) implements EventResult {

    @Override
    public long millisecondsToView() {
        return 5000;
    }

    @Override
    public List<Notification<?>> forCurrentPlayer() {
        var notification = new Notification<>(PLAYERS_CONQUEST_SUCCEEDED_NOTIFICATION,
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
        var notification = new Notification<>(ENEMY_CONQUEST_SUCCEEDED_NOTIFICATION,
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
}
