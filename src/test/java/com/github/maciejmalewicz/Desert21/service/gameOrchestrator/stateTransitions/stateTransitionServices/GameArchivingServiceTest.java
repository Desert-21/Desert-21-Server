package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameArchivingServiceTest extends AfterEachDatabaseCleanupExtension {

    @Autowired
    private GameRepository gameRepository;
    private Game game;

    @Autowired
    private GameArchivingService tested;

    @BeforeEach
    void setup() {
        game = new Game(
                List.of(
                        new Player("AA",
                                "macior123456",
                                new ResourceSet(60, 60, 60)),
                        new Player("BB",
                                "schabina123456",
                                new ResourceSet(60, 60, 60))),
                BoardUtils.generateEmptyPlain(7),
                new StateManager(
                        GameState.FINISHED,
                        DateUtils.millisecondsFromNow(10000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
    }

    @Test
    void stateTransition() {
        tested.stateTransition(game);
        var fromRepo = gameRepository.findAll();
        assertEquals(0, fromRepo.size());
    }
}