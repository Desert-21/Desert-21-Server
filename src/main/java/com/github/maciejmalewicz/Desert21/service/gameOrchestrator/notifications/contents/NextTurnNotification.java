package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents;

import java.util.Date;

public record NextTurnNotification(String currentPlayerId, Date timeout, int turnCounter) {
}
