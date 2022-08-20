package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameArchivingService extends StateTransitionService {

    public final GameRepository gameRepository;

    @Autowired
    public GameArchivingService(GameRepository gameRepository) {
        super(null, null, null);
        this.gameRepository = gameRepository;
    }

    @Override
    public void stateTransition(Game gameBefore) {
        gameRepository.deleteById(gameBefore.getId());
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        return Optional.empty();
    }

    @Override
    protected long getTimeToWaitForTimeout(Game game) {
        return 0;
    }

    @Override
    protected Game changeGameState(Game game) {
        return null;
    }
}
