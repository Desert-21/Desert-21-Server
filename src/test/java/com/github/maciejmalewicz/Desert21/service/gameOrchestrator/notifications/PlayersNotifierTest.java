package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.core.MessageSendingOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PlayersNotifierTest {

    record AnyObj (String arg) {}

    private PlayersNotifier tested;

    private MessageSendingOperations<String> msgOperations;

    private Game game;


    @Autowired
    private AiPlayerConfig aiPlayerConfig;

    @BeforeEach
    void setup() {
        msgOperations = mock(MessageSendingOperations.class);
        tested = new PlayersNotifier(msgOperations, aiPlayerConfig);
        setupGame();
    }

    void setupGame() {
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

    @Test
    void notifyPlayersWithPair() {
        var argumentCaptorTopic = ArgumentCaptor.forClass(String.class);
        var argumentCaptorContent = ArgumentCaptor.forClass(Notification.class);

        var notificationPair = new PlayersNotificationPair(
                new Notification<>("N1", new AnyObj("n1")),
                new Notification<>("N2", new AnyObj("n2"))
        );
        tested.notifyPlayers(game, notificationPair);

        verify(msgOperations, times(2)).convertAndSend(
                argumentCaptorTopic.capture(),
                argumentCaptorContent.capture()
        );
        var calledTopics = argumentCaptorTopic.getAllValues();
        var calledContents = argumentCaptorContent.getAllValues();

        var topic1 = "/topics/users/AA";
        var topic2 = "/topics/users/BB";
        var content1 = new Notification<>("N1", new AnyObj("n1"));
        var content2 = new Notification<>("N2", new AnyObj("n2"));

        assertEquals(topic1, calledTopics.get(0));
        assertEquals(topic2, calledTopics.get(1));
        assertEquals(content1, calledContents.get(0));
        assertEquals(content2, calledContents.get(1));
    }

    @Test
    void notifyPlayersWithEmptyCurrentPlayer() {
        game.getStateManager().setCurrentPlayerId(null);

        var argumentCaptorTopic = ArgumentCaptor.forClass(String.class);
        var argumentCaptorContent = ArgumentCaptor.forClass(Notification.class);

        var notificationPair = new PlayersNotificationPair(
                new Notification<>("N1", new AnyObj("n1")),
                new Notification<>("N2", new AnyObj("n2"))
        );
        tested.notifyPlayers(game, notificationPair);

        verify(msgOperations, times(2)).convertAndSend(
                argumentCaptorTopic.capture(),
                argumentCaptorContent.capture()
        );
        var calledTopics = argumentCaptorTopic.getAllValues();
        var calledContents = argumentCaptorContent.getAllValues();

        var topic1 = "/topics/users/AA";
        var topic2 = "/topics/users/BB";
        var content1 = new Notification<>("N1", new AnyObj("n1"));
        var content2 = new Notification<>("N1", new AnyObj("n1"));

        assertEquals(topic1, calledTopics.get(0));
        assertEquals(topic2, calledTopics.get(1));
        assertEquals(content1, calledContents.get(0));
        assertEquals(content2, calledContents.get(1));
    }

    @Test
    void testNotifyPlayersForBoth() {
        var notification = new Notification<>("N1", new AnyObj("n1"));
        tested.notifyPlayers(game, notification);

        var argumentCaptorTopic = ArgumentCaptor.forClass(String.class);
        var argumentCaptorContent = ArgumentCaptor.forClass(Notification.class);

        verify(msgOperations, times(2)).convertAndSend(
                argumentCaptorTopic.capture(),
                argumentCaptorContent.capture()
        );

        var calledTopics = argumentCaptorTopic.getAllValues();
        var calledContents = argumentCaptorContent.getAllValues();

        var topic1 = "/topics/users/AA";
        var topic2 = "/topics/users/BB";
        var content1 = new Notification<>("N1", new AnyObj("n1"));
        var content2 = new Notification<>("N1", new AnyObj("n1"));

        assertEquals(topic1, calledTopics.get(0));
        assertEquals(topic2, calledTopics.get(1));
        assertEquals(content1, calledContents.get(0));
        assertEquals(content2, calledContents.get(1));
    }

    @Test
    void testNotifyPlayerNormalCase() {
        var notification = new Notification<>("N1", new AnyObj("n1"));
        tested.notifyPlayer("AA", notification);

        var argumentCaptorTopic = ArgumentCaptor.forClass(String.class);
        var argumentCaptorContent = ArgumentCaptor.forClass(Notification.class);

        verify(msgOperations, times(1)).convertAndSend(
                argumentCaptorTopic.capture(),
                argumentCaptorContent.capture()
        );

        var calledTopic = argumentCaptorTopic.getValue();
        var calledContent = argumentCaptorContent.getValue();

        var topic = "/topics/users/AA";
        var content = new Notification<>("N1", new AnyObj("n1"));

        assertEquals(topic, calledTopic);
        assertEquals(content, calledContent);
    }

    @Test
    void testNotifyPlayerAICase() {
        var notification = new Notification<>("N1", new AnyObj("n1"));
        tested.notifyPlayer(aiPlayerConfig.getId(), notification);

        verify(msgOperations, never()).convertAndSend(
                anyString(),
                any(Notification.class)
        );
    }
}