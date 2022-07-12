package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.ROCKET_LAUNCHER;

public class RocketLaunchersRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        var middle = boardSize / 2;
        var almostLast = boardSize - 2;
        return List.of(
                new BoardLocationRule(List.of(new Location(1, 1)), ROCKET_LAUNCHER, 1),
                new BoardLocationRule(List.of(new Location(middle, middle)), ROCKET_LAUNCHER, 1),
                new BoardLocationRule(List.of(new Location(almostLast, almostLast)), ROCKET_LAUNCHER, 1)
        );
    }
}
