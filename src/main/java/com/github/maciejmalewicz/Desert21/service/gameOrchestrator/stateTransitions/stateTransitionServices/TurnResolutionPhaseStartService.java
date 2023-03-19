package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.ai.core.AiTurnHandler;
import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.ResolutionPhaseNotificationService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.ResolutionPhaseNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.GameEventsExecutionService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.RESOLUTION_PHASE_NOTIFICATION;

@Service
public class TurnResolutionPhaseStartService extends StateTransitionService {

    private final ResolutionPhaseNotificationService notificationService;
    private final GameEventsExecutionService gameEventsExecutionService;
    private final GameBalanceService gameBalanceService;
    private final AiPlayerConfig aiPlayerConfig;
    private final AiTurnHandler aiTurnHandler;

    public TurnResolutionPhaseStartService(PlayersNotifier playersNotifier,
                                           TimeoutExecutor timeoutExecutor,
                                           GameRepository gameRepository, ResolutionPhaseNotificationService notificationService, GameEventsExecutionService gameEventsExecutionService, GameBalanceService gameBalanceService, AiPlayerConfig aiPlayerConfig, AiTurnHandler aiTurnHandler) {
        super(playersNotifier, timeoutExecutor, gameRepository);
        this.notificationService = notificationService;
        this.gameEventsExecutionService = gameEventsExecutionService;
        this.gameBalanceService = gameBalanceService;
        this.aiPlayerConfig = aiPlayerConfig;
        this.aiTurnHandler = aiTurnHandler;
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        var notificationPair = notificationService.createNotifications(game);
        return Optional.of(new PlayersNotificationPair(
                new Notification<>(
                        RESOLUTION_PHASE_NOTIFICATION,
                        new ResolutionPhaseNotification(
                                game.getStateManager().getTimeout(),
                                notificationPair.forCurrentPlayer(),
                                game.getId()
                        )),
                new Notification<>(
                        RESOLUTION_PHASE_NOTIFICATION,
                        new ResolutionPhaseNotification(
                                game.getStateManager().getTimeout(),
                                notificationPair.forOpponent(),
                                game.getId()
                        ))
        ));
    }

    @Override
    protected long getTimeToWaitForTimeout(Game game) {
        return game.getCurrentEventResults().stream()
                .map(EventResult::millisecondsToView)
                .reduce(Long::sum)
                .orElse(0L);
    }

    @Override
    protected Game changeGameState(Game game) {
        var stateManger = game.getStateManager();
        if (aiPlayerConfig.isAiTurn(game)) {
            aiTurnHandler.handleTurn(game);
            stateManger.setCurrentlyTimedOut(false);
        }
        stateManger.setGameState(GameState.RESOLVED);

        if (stateManger.isCurrentlyTimedOut()) {
            game = executeEventsAndProduceResources(game);
        }
        return game;
    }

    private Game executeEventsAndProduceResources(Game game) {
        try {
            var eventExecutionResults = gameEventsExecutionService
                    .executeEvents(new ArrayList<>(), fetchTurnExecutionContext(game));
            game = eventExecutionResults.context().game();
            game.setCurrentEventResults(eventExecutionResults.results());
            return game;
        } catch (NotAcceptableException exc) {
            //ignore
            return game;
        }
    }

    private TurnExecutionContext fetchTurnExecutionContext(Game game) {
        return new TurnExecutionContext(
                gameBalanceService.getGameBalance(),
                game,
                game.getCurrentPlayer().orElseThrow() //may be ignored
        );
    }
}
