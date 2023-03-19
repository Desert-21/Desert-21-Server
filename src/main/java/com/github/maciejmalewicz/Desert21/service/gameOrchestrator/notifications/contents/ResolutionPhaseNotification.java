package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;

import java.util.Date;
import java.util.List;

public record ResolutionPhaseNotification(Date timeout, List<Notification<?>> notifications, String gameId) {
}
