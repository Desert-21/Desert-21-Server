package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public interface Notifiable {

    default List<Notification<?>> forProducer() {
        return new ArrayList<>();
    }

    default List<Notification<?>> forOpponent() {
        return new ArrayList<>();
    }

    default Pair<String, List<Notification<?>>> forSpecificPlayer() {
        return Pair.of("", new ArrayList<>());
    }

    default List<Notification<?>> forBoth() {
        return new ArrayList<>();
    }
}
