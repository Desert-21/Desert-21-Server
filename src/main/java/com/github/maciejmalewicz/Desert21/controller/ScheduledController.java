package com.github.maciejmalewicz.Desert21.controller;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
public class ScheduledController {
    private final MessageSendingOperations<String> messageSendingOperations;
    public ScheduledController(MessageSendingOperations<String> messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    @Scheduled(fixedDelay = 1000)
    public void sendPeriodicMessages() {
        String broadcast = String.format("server periodic message %s via the broker", LocalTime.now());
        try {
            this.messageSendingOperations.convertAndSend("/topics/users/61fff82fc758dd5627fb2183", broadcast);
        } catch (MessagingException exc) {
            exc.printStackTrace();
        }

    }
}