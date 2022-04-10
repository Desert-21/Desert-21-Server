package com.github.maciejmalewicz.Desert21.service.playersQueue;

import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

class PlayersQueueServiceTest {

    private GameGeneratorService gameGeneratorService;
    private PlayersQueueService tested;

    @BeforeEach
    void setup() {
        gameGeneratorService = mock(GameGeneratorService.class);
        tested = new PlayersQueueService(gameGeneratorService);
    }

    @Test
    void addTwoPlayersToQueueNormalCase() {
        var id = "AA";
        tested.addPlayerToQueue(id);

        verify(gameGeneratorService, never()).generateGame(anyString(), anyString());

        var id2 = "BB";
        tested.addPlayerToQueue(id2);

        verify(gameGeneratorService, times(1)).generateGame(id, id2);
    }

    @Test
    void addOnePlayerRepeatedRequest() {
        var id = "AA";
        tested.addPlayerToQueue(id);

        verify(gameGeneratorService, never()).generateGame(anyString(), anyString());

        tested.addPlayerToQueue(id);
        verify(gameGeneratorService, never()).generateGame(anyString(), anyString());
    }

    @Test
    void addingAndRemovingPlayers() {
        var id1 = "AA";
        var id2 = "BB";

        tested.addPlayerToQueue(id1);
        tested.removePlayerFromQueue(id1);
        tested.addPlayerToQueue(id2);

        verify(gameGeneratorService, never()).generateGame(anyString(), anyString());

        tested.removePlayerFromQueue(id1);
        verify(gameGeneratorService, never()).generateGame(anyString(), anyString());

        tested.addPlayerToQueue(id1);
        verify(gameGeneratorService, times(1)).generateGame(id2, id1);
    }

    @Test
    void highLoadAddingPlayers() {
        IntStream.range(0, 10_000)
                .mapToObj(i -> new Thread(
                        () -> tested.addPlayerToQueue(Integer.toString(i)))
                )
                .forEach(Thread::start);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            //ignore
        }
        verify(gameGeneratorService, times(5_000))
                .generateGame(anyString(), anyString());
    }
}