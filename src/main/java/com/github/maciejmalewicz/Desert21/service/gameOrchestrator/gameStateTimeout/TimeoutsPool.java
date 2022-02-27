package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.gameStateTimeout;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
class TimeoutsPool {

    private HashMap<String, Object> gameIdToTimeout = new HashMap<>();

    private final GameRepository gameRepository;

    public TimeoutsPool(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.gameRepository.findAll().forEach(this::restoreTimeoutOfGame);
    }

    public synchronized boolean addTimeoutIfAbsent(GameStateTimeout gameStateTimeout) {
        return gameIdToTimeout.putIfAbsent(gameStateTimeout.gameId(), gameStateTimeout) == null;
    }

    public synchronized void removeTimeout(String gameId) {
        gameIdToTimeout.remove(gameId);
    }

    private void restoreTimeoutOfGame(Game game) {

    }
}
