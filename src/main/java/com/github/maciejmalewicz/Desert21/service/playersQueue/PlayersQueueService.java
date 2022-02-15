package com.github.maciejmalewicz.Desert21.service.playersQueue;

import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class PlayersQueueService {

    private String playerIdInQueue = null;

    private final GameGeneratorService gameGeneratorService;

    public PlayersQueueService(GameGeneratorService gameGeneratorService) {
        this.gameGeneratorService = gameGeneratorService;
    }


    public synchronized void addPlayerToQueue(String id) {
        if (playerIdInQueue != null) {
            gameGeneratorService.generateGame(playerIdInQueue, id);
            playerIdInQueue = null;
            return;
        }
        this.playerIdInQueue = id;
    }
}
