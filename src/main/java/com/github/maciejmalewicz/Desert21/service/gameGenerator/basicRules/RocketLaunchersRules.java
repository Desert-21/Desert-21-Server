package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.misc.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.misc.BuildingType.ROCKET_LAUNCHER;

public class RocketLaunchersRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules() {
        return List.of(
                new BoardLocationRule(List.of(new Location(1, 1)), ROCKET_LAUNCHER, 1),
                new BoardLocationRule(List.of(new Location(5, 5)), ROCKET_LAUNCHER, 1),
                new BoardLocationRule(List.of(new Location(9, 9)), ROCKET_LAUNCHER, 1)
        );
    }
}
