package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.stream.Stream;

@Service
public class PlayersNotifier {

    private final MessageSendingOperations<String> messageSendingOperations;

    public PlayersNotifier(MessageSendingOperations<String> messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    public void notifyPlayers(Game game, PlayersNotificationPair notification) {
        var currentPlayerId = game.getCurrentPlayer()
                .map(Player::getId);
        var otherPlayerId = game.getOtherPlayer()
                .map(Player::getId);

        if (currentPlayerId.isEmpty() || otherPlayerId.isEmpty()) {
            notifyPlayers(game, notification.forCurrentPlayer());
            return;
        }

        var currentPlayerTopic = String.format("/topics/users/%s", currentPlayerId.get());
        messageSendingOperations.convertAndSend(currentPlayerTopic, notification.forCurrentPlayer());

        var otherPlayerTopic = String.format("/topics/users/%s", otherPlayerId.get());
        messageSendingOperations.convertAndSend(otherPlayerTopic, notification.forOpponent());
    }

    public void notifyPlayers(Game game, Notification<?> notification) {
        game.getPlayers().forEach(p -> {
            var id = p.getId();
            var topic = String.format("/topics/users/%s", id);
            messageSendingOperations.convertAndSend(topic, notification);
        });
    }

    public void notifyPlayer(String id, Notification<?> notification) {
        var topic = String.format("/topics/users/%s", id);
        messageSendingOperations.convertAndSend(topic, notification);
    }
}
