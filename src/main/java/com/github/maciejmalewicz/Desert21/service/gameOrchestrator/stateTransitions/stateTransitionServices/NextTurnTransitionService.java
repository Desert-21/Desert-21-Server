package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;

@Service
public class NextTurnTransitionService extends StateTransitionService {

    public NextTurnTransitionService(PlayersNotifier playersNotifier, TimeoutExecutor timeoutExecutor, GameRepository gameRepository) {
        super(playersNotifier, timeoutExecutor, gameRepository);
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        var currentPlayerId = game.getCurrentPlayer()
                .map(Player::getId)
                .orElse("");
        return Optional.of(PlayersNotificationPair.forBoth(
                new Notification<>(NEXT_TURN_NOTIFICATION, new NextTurnNotification(currentPlayerId, game.getStateManager().getTimeout()))
        ));
    }

    @Override
    protected long getTimeToWaitForTimeout(Game game) {
        return 15_000;
    }

    @Override
    protected Game changeGameState(Game game) {
        var idOpt = game.getOtherPlayer().map(Player::getId);
        idOpt.ifPresent(id -> {
            game.getStateManager().setCurrentPlayerId(id);
            var isFirstPlayer = game.getStateManager().getCurrentPlayerId().equals(
                    game.getStateManager().getFirstPlayerId()
            );
            if (isFirstPlayer) {
                game.getStateManager().setTurnCounter(game.getStateManager().getTurnCounter() + 1);
            }
        });
        game.getStateManager().setGameState(GameState.AWAITING);
        return game;
    }
}
