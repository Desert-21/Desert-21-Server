package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import java.util.ArrayList;
import java.util.List;

public interface Notifiable {

    default List<Notification<?>> forProducer() {
        return new ArrayList<>();
    }

    default List<Notification<?>> forFieldOwner() {
        return new ArrayList<>();
    }

    default List<Notification<?>> forBoth() {
        return new ArrayList<>();
    }
}
