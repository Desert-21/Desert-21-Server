package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.LabUpgradeEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.LabUpgradeEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LabUpgradeExecutor implements EventExecutor<LabUpgradeEvent> {

    @Override
    public EventExecutionResult execute(List<LabUpgradeEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var eventResults = new ArrayList<EventResult>();
        var player = context.player();
        for (LabUpgradeEvent event: events) {
            var upgrade = event.getUpgrade();
            player.getOwnedUpgrades().add(upgrade);
            var eventResult = new LabUpgradeEventResult(upgrade, player.getId());
            eventResults.add(eventResult);

            if (upgrade == LabUpgrade.PRODUCTION_AI) {
                activateProductionAI(player);
            }

            if (upgrade == LabUpgrade.SUPER_SONIC_ROCKETS) {
                activateSuperSonicRocketsDiscount(player);
            }
        }
        return new EventExecutionResult(context, eventResults);
    }

    private void activateProductionAI(Player player) {
        player.getProductionAI().setActivated(true);
    }

    private void activateSuperSonicRocketsDiscount(Player player) {
        player.setNextRocketFree(true);
    }
}
