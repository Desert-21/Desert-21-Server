package com.github.maciejmalewicz.Desert21.ai.core;

import com.github.maciejmalewicz.Desert21.ai.helpers.AiTurnExecutionContext;
import com.github.maciejmalewicz.Desert21.ai.helpers.FieldEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.ai.helpers.GameEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.GameEventsExecutionService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.PlayersActionsValidatingService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

@Service
public class AiTurnExecutor {

    private final PlayersActionsValidatingService playersActionsValidatingService;
    private final GameEventsExecutionService gameEventsExecutionService;
    private final AiTurnActionsGenerator aiTurnActionsGenerator;

    public AiTurnExecutor(PlayersActionsValidatingService playersActionsValidatingService, GameEventsExecutionService gameEventsExecutionService, AiTurnActionsGenerator aiTurnActionsGenerator) {
        this.playersActionsValidatingService = playersActionsValidatingService;
        this.gameEventsExecutionService = gameEventsExecutionService;
        this.aiTurnActionsGenerator = aiTurnActionsGenerator;
    }

    public void executeTurn(Game game, Player player, GameBalanceDto gameBalance) {
        var aiContext = constructAiContext(game, player, gameBalance);
        var actions = aiTurnActionsGenerator.getFullActionChoice(aiContext);

        var context = new TurnExecutionContext(gameBalance, game, player);
        var mayExecuteActions = playersActionsValidatingService.validatePlayersActions(actions, context);
        if (!mayExecuteActions) {
            throw new RuntimeException("AI is working incorrectly");
        }
        EventExecutionResult eventExecutionResults = null;
        try {
            eventExecutionResults = gameEventsExecutionService.executeEvents(actions, context);
        } catch (NotAcceptableException e) {
            throw new RuntimeException("AI is working incorrectly");
        }
        var updatedContext = eventExecutionResults.context();
        var eventResults = eventExecutionResults.results();
        var updatedGame = updatedContext.game();

        //setting up for the next phase
        updatedGame.setCurrentEventResults(eventResults);
    }

    private AiTurnExecutionContext constructAiContext(Game game, Player player, GameBalanceDto gameBalance) {
        var enhancedGameWrapper = new GameEnhancementWrapper(game);
        game.getEventQueue().forEach(event -> {
            if (event.getClass() == BuildBuildingEvent.class) {
                handleBuildingEvent(enhancedGameWrapper, (BuildBuildingEvent) event);
            }
            if (event.getClass() == ArmyTrainingEvent.class) {
                handleTrainingEvent(enhancedGameWrapper, (ArmyTrainingEvent) event);
            }
        });
        return new AiTurnExecutionContext(
                enhancedGameWrapper,
                player,
                gameBalance
        );
    }

    private void handleTrainingEvent(GameEnhancementWrapper enhancedGameWrapper, ArmyTrainingEvent trainEvent) {
        try {
            var field = (FieldEnhancementWrapper) BoardUtils.fieldAtLocation(
                    enhancedGameWrapper.getFields(),
                    trainEvent.getLocation()
            );
            field.setAlreadyTrainingHere(true);
        } catch (NotAcceptableException e) {
            throw new RuntimeException();
        }
    }

    private void handleBuildingEvent(GameEnhancementWrapper enhancedGameWrapper, BuildBuildingEvent buildEvent) {
        try {
            var field = (FieldEnhancementWrapper) BoardUtils.fieldAtLocation(
                    enhancedGameWrapper.getFields(),
                    buildEvent.getLocation()
            );
            field.setAlreadyBuildingHere(true);
        } catch (NotAcceptableException e) {
            throw new RuntimeException();
        }
    }
}
