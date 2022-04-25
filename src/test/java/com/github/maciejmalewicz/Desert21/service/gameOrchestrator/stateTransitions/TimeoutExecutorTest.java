package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables.GameStartTimeoutExecutable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables.TimeoutExecutable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.StateTransitionService;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class TimeoutExecutorTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameStartTimeoutExecutable executable;

    private TimeoutExecutor tested;

    private Game game;

    private StateTransitionService stateTransitionService;

    private PlayersNotifier playersNotifier;

    private Notification<?> notification;

    void setupTested() {
        var executablePicker = mock(TimeoutExecutablePicker.class);
        stateTransitionService = mock(StateTransitionService.class);
        doNothing().when(stateTransitionService).stateTransition(any(Game.class));
        notification = new Notification<>("NOTIFICATION_TYPE", null);
        var executable = new TimeoutExecutable() {
            @Override
            public Optional<Notification<?>> getNotifications(Game game) {
                return Optional.of(notification);
            }

            @Override
            public StateTransitionService getStateTransitionService(Game game) {
                return stateTransitionService;
            }

            @Override
            public long getExecutionOffset() {
                return 500;
            }
        };
        doReturn(executable).when(executablePicker).pickTimeoutExecutable(any(Game.class));

        playersNotifier = mock(PlayersNotifier.class);
        tested = new TimeoutExecutor(
                executablePicker,
                gameRepository,
                playersNotifier
        );
    }


    void setupGame(long timeout) {
        game = new Game(
                List.of(
                        new Player("AA",
                                "macior123456",
                                new ResourceSet(60, 60, 60)),
                        new Player("BB",
                                "schabina123456",
                                new ResourceSet(60, 60, 60))),
                new Field[9][9],
                new StateManager(
                        GameState.WAITING_TO_START,
                        DateUtils.millisecondsFromNow(timeout),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
    }

    void setupTests(long timeout) {
        setupTested();
        setupGame(timeout);
    }

    @Test
    void executeTimeoutOnGameHappyPath() {
        setupTests(100);

        tested.executeTimeoutOnGame(game);
        verifyNotExecuted();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException exc) {
            exc.printStackTrace();
            //ignore
        }
        verifyExecuted();
    }

    @Test
    void executeTimeoutOnGameTimeoutExpired() {
        setupTests(100);

        tested.executeTimeoutOnGame(game);
        verifyNotExecuted();

        //timeout expires and is overwritten by other
        var savedGameOpt = gameRepository.findAll().stream().findAny();
        assertTrue(savedGameOpt.isPresent());
        var savedGame = savedGameOpt.get();
        savedGame.getStateManager().setCurrentStateTimeoutId("OTHER+TIMEOUT_ID");
        gameRepository.save(savedGame);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException exc) {
            exc.printStackTrace();
            //ignore
        }
        verifyNotExecuted();
    }

    //when game finishes before timeout execution
    @Test
    void executeTimeoutOnGameExpired() {
        setupTests(100);

        tested.executeTimeoutOnGame(game);
        verifyNotExecuted();

        //timeout expires and is overwritten by other
        var savedGameOpt = gameRepository.findAll().stream().findAny();
        assertTrue(savedGameOpt.isPresent());
        var savedGame = savedGameOpt.get();
        gameRepository.delete(savedGame);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException exc) {
            exc.printStackTrace();
            //ignore
        }
        verifyNotExecuted();
    }

    //when restarting game -- to be changed in the future
    @Test
    void executeNegativeTimeoutAndNotCrash() {
        setupTests(-10000);

        tested.executeTimeoutOnGame(game);
        verifyNotExecuted();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException exc) {
            exc.printStackTrace();
            //ignore
        }
        verifyExecuted();
    }

    void verifyNotExecuted() {
        verify(
                stateTransitionService,
                never())
                .stateTransition(any(Game.class));
        verify(
                playersNotifier,
                never()
        ).notifyPlayers(any(Game.class), any(Notification.class));
    }

    void verifyExecuted() {
        verify(
                stateTransitionService,
                times(1))
                .stateTransition(game);
        verify(
                playersNotifier,
                times(1)
        ).notifyPlayers(game, notification);
    }
}