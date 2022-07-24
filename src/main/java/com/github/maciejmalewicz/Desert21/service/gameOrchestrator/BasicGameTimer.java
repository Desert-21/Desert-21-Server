package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.github.maciejmalewicz.Desert21.utils.BoardUtils.boardToFieldList;

@Component
public class BasicGameTimer implements GameTimer {

    @Override
    public long getInitialTime() {
        return 15_000;
    }

    @Override
    public long getMoveTime(Game game) {
        var totalOccupiedFields = boardToFieldList(game.getFields()).stream()
                        .filter(f -> f.getOwnerId() != null)
                        .count();
        return 30_000 + totalOccupiedFields * 2_000;
    }
}
