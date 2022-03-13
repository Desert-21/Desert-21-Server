package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.domain.games.StateManager;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;

@Service
public class NextTurnStarterService {

    private final BasicGameTimer basicGameTimer;
    private final PlayersNotifier playersNotifier;
    private final GameRepository gameRepository;

    public NextTurnStarterService(BasicGameTimer basicGameTimer, PlayersNotifier playersNotifier, GameRepository gameRepository) {
        this.basicGameTimer = basicGameTimer;
        this.playersNotifier = playersNotifier;
        this.gameRepository = gameRepository;
    }

    public void startNextTurn(Game game) {
        var nextPlayer = nextPLayer(game);
        var moveTimeout = DateUtils.millisecondsFromNow(basicGameTimer.getMoveTime(game));
        game.setStateManager(new StateManager(GameState.AWAITING, moveTimeout, nextPlayer.getId(), null)); //will change
        gameRepository.save(game);

        var notification = new NextTurnNotification(nextPlayer.getId(), moveTimeout);
        var notifiable = new Notifiable() {
            @Override
            public List<Notification<?>> forBoth() {
                return List.of(new Notification<>(NEXT_TURN_NOTIFICATION, notification));
            }
        };
        playersNotifier.notifyPlayers(game, notifiable);
    }

    private Player nextPLayer(Game game) {
        var currentPlayerId = game.getStateManager().getCurrentPlayerId();
        if (currentPlayerId == null) {
            return game.getPlayers().stream().findAny().get();
        }
        return game.getPlayers().stream().filter(p -> !p.getId().equals(currentPlayerId)).findAny().get();
    }
}
