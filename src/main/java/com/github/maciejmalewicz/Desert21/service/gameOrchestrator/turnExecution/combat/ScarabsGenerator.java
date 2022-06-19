package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.utils.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class ScarabsGenerator {

    public int generateScarabs(TurnExecutionContext context) {
        var scarabConfig = context.gameBalance().combat().scarabs();
        var turnNumber = context.game().getStateManager().getTurnCounter();
        var middleScarabsNumber =
                scarabConfig.getBaseGeneration() + turnNumber * scarabConfig.getAdditionalGenerationPerTurn();
        var minScarabs = (int) Math.round(
                middleScarabsNumber * (1 - scarabConfig.getGenerationBias())
        );
        var maxScarabs = (int) Math.round(
                middleScarabsNumber * (1 + scarabConfig.getGenerationBias())
        );
        return RandomGenerator.generateBetween(minScarabs, maxScarabs);
    }
}
