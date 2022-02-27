package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

public record Notification<CONTENT> (String type, CONTENT content){

}
