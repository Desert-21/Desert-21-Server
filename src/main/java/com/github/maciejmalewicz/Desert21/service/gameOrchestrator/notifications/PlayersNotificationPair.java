package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

public record PlayersNotificationPair(Notification<?> forCurrentPlayer, Notification<?> forOpponent) {

    public static PlayersNotificationPair forBoth(Notification<?> notification) {
        return new PlayersNotificationPair(notification, notification);
    }
}
