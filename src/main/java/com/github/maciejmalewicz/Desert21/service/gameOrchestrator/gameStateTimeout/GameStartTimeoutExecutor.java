package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.gameStateTimeout;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.NextTurnStarterService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GameStartTimeoutExecutor extends GameTimeoutExecutor {

    private final BasicGameTimer basicGameTimer;
    private final NextTurnStarterService nextTurnStarterService;

    protected GameStartTimeoutExecutor(GameRepository gameRepository,
                                       PlayersNotifier playersNotifier,
                                       TimeoutsPool timeoutsPool,
                                       BasicGameTimer basicGameTimer,
                                       NextTurnStarterService nextTurnStarterService) {
        super(gameRepository, playersNotifier, timeoutsPool);
        this.basicGameTimer = basicGameTimer;
        this.nextTurnStarterService = nextTurnStarterService;
    }

    @Override
    protected long getExecutionOffset() {
        return 0;
    }

    @Override
    protected Notifiable switchGameState(Game game) {
        nextTurnStarterService.startNextTurn(game);
        var notification = new NextTurnNotification("", new Date());
        return new Notifiable() {
            @Override
            public List<Notification<?>> forBoth() {
                return List.of(new Notification<>("START_GAME", notification));
            }
        };
    }
}
