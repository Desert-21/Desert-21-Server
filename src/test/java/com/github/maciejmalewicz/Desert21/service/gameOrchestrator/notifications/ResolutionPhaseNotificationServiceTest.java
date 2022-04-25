package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResolutionPhaseNotificationServiceTest {

    private ResolutionPhaseNotificationService tested;

    private Game game;

    @BeforeEach
    void setup() {
        tested = new ResolutionPhaseNotificationService();
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
                        GameState.AWAITING,
                        DateUtils.millisecondsFromNow(10_000),
                        "AA",
                        "TIMEOUTID"
                )
        );
    }

    static class TestResult implements EventResult {
        @Override
        public long millisecondsToView() {
            return 0;
        }
    }

    @Test
    void createNotificationsForBothSingle() {
        game.setCurrentEventResults(List.of(
                new TestResult() {
                    @Override
                    public List<Notification<?>> forBoth() {
                        return List.of(new Notification<>("N1", "n1"));
                    }
                }
        ));
        var resolutionPhaseNotificationPair = tested.createNotifications(game);
        assertEquals(new ResolutionPhaseNotificationPair(
                List.of(new Notification<>("N1", "n1")),
                List.of(new Notification<>("N1", "n1"))
        ), resolutionPhaseNotificationPair);
    }

    @Test
    void createNotificationsForCurrentPLayer() {
        game.setCurrentEventResults(List.of(
                new TestResult() {
                    @Override
                    public List<Notification<?>> forCurrentPlayer() {
                        return List.of(new Notification<>("N2", "n2"));
                    }
                }
        ));
        var resolutionPhaseNotificationPair = tested.createNotifications(game);
        assertEquals(new ResolutionPhaseNotificationPair(
                List.of(new Notification<>("N2", "n2")),
                new ArrayList<>()
        ), resolutionPhaseNotificationPair);
    }

    @Test
    void createNotificationsForOpponent() {
        game.setCurrentEventResults(List.of(
                new TestResult() {
                    @Override
                    public List<Notification<?>> forOpponent() {
                        return List.of(new Notification<>("N3", "n3"));
                    }
                }
        ));
        var resolutionPhaseNotificationPair = tested.createNotifications(game);
        assertEquals(new ResolutionPhaseNotificationPair(
                new ArrayList<>(),
                List.of(new Notification<>("N3", "n3"))
        ), resolutionPhaseNotificationPair);
    }

    @Test
    void createNotificationsSpecificPlayerCurrent() {
        game.setCurrentEventResults(List.of(
                new TestResult() {
                    @Override
                    public Pair<String, List<Notification<?>>> forSpecificPlayer() {
                        return Pair.of("AA", List.of(new Notification<>("N4", "n4")));
                    }
                }
        ));
        var resolutionPhaseNotificationPair = tested.createNotifications(game);
        assertEquals(new ResolutionPhaseNotificationPair(
                List.of(new Notification<>("N4", "n4")),
                new ArrayList<>()
        ), resolutionPhaseNotificationPair);
    }

    @Test
    void createNotificationsSpecificPlayerOpponent() {
        game.setCurrentEventResults(List.of(
                new TestResult() {
                    @Override
                    public Pair<String, List<Notification<?>>> forSpecificPlayer() {
                        return Pair.of("BB", List.of(new Notification<>("N5", "n5")));
                    }
                }
        ));
        var resolutionPhaseNotificationPair = tested.createNotifications(game);
        assertEquals(new ResolutionPhaseNotificationPair(
                new ArrayList<>(),
                List.of(new Notification<>("N5", "n5"))
        ), resolutionPhaseNotificationPair);
    }

    @Test
    void createNotificationsMixedUp() {
        game.setCurrentEventResults(List.of(
                new TestResult() {
                    @Override
                    public List<Notification<?>> forBoth() {
                        return List.of(new Notification<>("N1", "n1"));
                    }
                },
                new TestResult() {
                    @Override
                    public List<Notification<?>> forCurrentPlayer() {
                        return List.of(new Notification<>("N2", "n2"));
                    }
                },
                new TestResult() {
                    @Override
                    public List<Notification<?>> forOpponent() {
                        return List.of(new Notification<>("N3", "n3"));
                    }
                },
                new TestResult() {
                    @Override
                    public Pair<String, List<Notification<?>>> forSpecificPlayer() {
                        return Pair.of("AA", List.of(new Notification<>("N4", "n4")));
                    }
                }
        ));
        var resolutionPhaseNotificationPair = tested.createNotifications(game);
        assertEquals(new ResolutionPhaseNotificationPair(
                List.of(
                        new Notification<>("N1", "n1"),
                        new Notification<>("N2", "n2"),
                        new Notification<>("N4", "n4")
                ),
                List.of(
                        new Notification<>("N1", "n1"),
                        new Notification<>("N3", "n3")
                )
        ), resolutionPhaseNotificationPair);
    }
}