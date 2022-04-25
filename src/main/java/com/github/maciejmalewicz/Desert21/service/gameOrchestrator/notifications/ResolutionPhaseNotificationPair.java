package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import java.util.List;

public record ResolutionPhaseNotificationPair(
        List<Notification<?>> forCurrentPlayer,
        List<Notification<?>> forOpponent) {
}
