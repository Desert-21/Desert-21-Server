package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;

public interface GameTimer {

    /**
    How much time do players have before the start of the first turn in MS
     */
    long getInitialTime();

    /**
    How much time do players have per turn in MS based on turn number
     */
    long getMoveTime(Game game);
}
