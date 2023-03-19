package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class PlayersNotifier {

    private final static String TOPIC_TEMPLATE = "/topics/users/%s";

    private final MessageSendingOperations<String> messageSendingOperations;
    private final AiPlayerConfig aiPlayerConfig;

    public PlayersNotifier(MessageSendingOperations<String> messageSendingOperations, AiPlayerConfig aiPlayerConfig) {
        this.messageSendingOperations = messageSendingOperations;
        this.aiPlayerConfig = aiPlayerConfig;
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

        notifyPlayer(currentPlayerId.get(), notification.forCurrentPlayer());
        notifyPlayer(otherPlayerId.get(), notification.forOpponent());
    }

    public void notifyPlayers(Game game, Notification<?> notification) {
        game.getPlayers().forEach(p -> {
            var id = p.getId();
            notifyPlayer(id, notification);
        });
    }

    public void notifyPlayer(String id, Notification<?> notification) {
        if (id.equals(aiPlayerConfig.getId())) { // do not send anything to the AI user
            return;
        }
        var topic = String.format(TOPIC_TEMPLATE, id);
        messageSendingOperations.convertAndSend(topic, notification);
    }
}
