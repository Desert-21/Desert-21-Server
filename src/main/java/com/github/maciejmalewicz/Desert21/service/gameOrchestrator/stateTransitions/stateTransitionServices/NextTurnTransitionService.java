package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.GameFinishedNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.GameEndCheckingService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.GAME_END_NOTIFICATION;
import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;

@Service
public class NextTurnTransitionService extends StateTransitionService {

    private final BasicGameTimer gameTimer;
    private final GameEndCheckingService gameEndCheckingService;

    public NextTurnTransitionService(PlayersNotifier playersNotifier, TimeoutExecutor timeoutExecutor, GameRepository gameRepository, BasicGameTimer gameTimer, GameEndCheckingService gameEndCheckingService) {
        super(playersNotifier, timeoutExecutor, gameRepository);
        this.gameTimer = gameTimer;
        this.gameEndCheckingService = gameEndCheckingService;
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        if (game.getStateManager().getGameState() == GameState.AWAITING) {
            var currentPlayerId = game.getCurrentPlayer()
                    .map(Player::getId)
                    .orElse("");
            return Optional.of(PlayersNotificationPair.forBoth(
                    new Notification<>(NEXT_TURN_NOTIFICATION, new NextTurnNotification(currentPlayerId, game.getStateManager().getTimeout()))
            ));
        }
        return Optional.of(PlayersNotificationPair.forBoth(new Notification<>(GAME_END_NOTIFICATION,
                new GameFinishedNotification(game.getStateManager().getWinnerId()))));
    }

    @Override
    protected long getTimeToWaitForTimeout(Game game) {
        if (game.getStateManager().getGameState() == GameState.FINISHED) {
            return 10_000;
        }
        return gameTimer.getMoveTime(game);
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
        var winnerIdOptional = gameEndCheckingService.checkIfGameHasEnded(game);
        if (winnerIdOptional.isPresent()) {
            game.getStateManager().setGameState(GameState.FINISHED);
            game.getStateManager().setWinnerId(winnerIdOptional.get());
            return game;
        }
        game.getStateManager().setGameState(GameState.AWAITING);
        return game;
    }
}
