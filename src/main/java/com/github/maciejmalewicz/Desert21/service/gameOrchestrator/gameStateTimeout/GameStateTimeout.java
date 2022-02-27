package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.gameStateTimeout;

import java.util.Date;

public record GameStateTimeout (
        String timeoutId,
        Date timeout,
        String gameId
){
}
