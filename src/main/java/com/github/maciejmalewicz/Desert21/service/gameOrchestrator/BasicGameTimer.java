package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import org.springframework.stereotype.Component;

@Component
public class BasicGameTimer implements GameTimer {

    @Override
    public long getInitialTime() {
        return 15_000;
    }

    @Override
    public long getMoveTime(Game game) {
        return 10_000;
    }
}
