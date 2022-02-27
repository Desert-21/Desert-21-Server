package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class PlayersNotifier {

    private final MessageSendingOperations<String> messageSendingOperations;

    public PlayersNotifier(MessageSendingOperations<String> messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    public void notifyPlayers(Game game, Notifiable notifiable) {
        game.getPlayers().forEach(p -> {
            var id = p.getId();
            var topic = String.format("/topics/users/%s", id);
            messageSendingOperations.convertAndSend(topic, notifiable.forBoth());
        });
    }

    public void notifyPlayers(Game game, Notifiable notifiable, String invokersId) {

    }
}
