package com.github.maciejmalewicz.Desert21.service.playersQueue;

import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
