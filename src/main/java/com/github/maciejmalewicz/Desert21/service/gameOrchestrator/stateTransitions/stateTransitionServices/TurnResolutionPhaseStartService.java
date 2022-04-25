package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.ResolutionPhaseNotificationService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.ResolutionPhaseNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.RESOLUTION_PHASE_NOTIFICATION;

@Service
public class TurnResolutionPhaseStartService extends StateTransitionService {

    private final ResolutionPhaseNotificationService notificationService;

    public TurnResolutionPhaseStartService(PlayersNotifier playersNotifier,
                                           TimeoutExecutor timeoutExecutor,
                                           GameRepository gameRepository, ResolutionPhaseNotificationService notificationService) {
        super(playersNotifier, timeoutExecutor, gameRepository);
        this.notificationService = notificationService;
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        var notificationPair = notificationService.createNotifications(game);
        return Optional.of(new PlayersNotificationPair(
                new Notification<>(
                        RESOLUTION_PHASE_NOTIFICATION,
                        new ResolutionPhaseNotification(
                                game.getStateManager().getTimeout(),
                                notificationPair.forCurrentPlayer()
                        )),
                new Notification<>(
                        RESOLUTION_PHASE_NOTIFICATION,
                        new ResolutionPhaseNotification(
                                game.getStateManager().getTimeout(),
                                notificationPair.forOpponent()
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
        stateManger.setGameState(GameState.RESOLVED);
        return game;
    }
}
