package com.github.maciejmalewicz.Desert21.service.playersQueue;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.StartGameNotification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.github.maciejmalewicz.Desert21.config.Constants.START_GAME_NOTIFICATION;

//very basic queue, definitely will be improved when the game has trillions of players :)
@Service
public class PlayersQueueService {

    private String playerIdInQueue = null;

    private final GameGeneratorService gameGeneratorService;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public PlayersQueueService(GameGeneratorService gameGeneratorService) {
        this.gameGeneratorService = gameGeneratorService;
    }

    public void addPlayerToQueue(String id) {
        lock.writeLock().lock();
        try {
            if (id.equals(playerIdInQueue)) {
                return; //ignoring
            }
            if (playerIdInQueue != null) {
                gameGeneratorService.generateGame(playerIdInQueue, id);
                playerIdInQueue = null;
                return;
            }
            this.playerIdInQueue = id;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public synchronized void removePlayerFromQueue(String id) {
        lock.writeLock().lock();
        try {
            if (id.equals(playerIdInQueue)) {
                this.playerIdInQueue = null;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
