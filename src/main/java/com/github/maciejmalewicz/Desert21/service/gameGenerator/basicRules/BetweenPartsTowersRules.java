package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.TOWER;

public class BetweenPartsTowersRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        var twoBeforeEnd = boardSize - 2;
        var middle = boardSize / 2;
        return List.of(
                new BoardLocationRule(List.of(new Location(1, middle)), TOWER, 1),
                new BoardLocationRule(List.of(new Location(middle, 1)), TOWER, 1),
                new BoardLocationRule(List.of(new Location(middle, twoBeforeEnd)), TOWER, 1),
                new BoardLocationRule(List.of(new Location(twoBeforeEnd, middle)), TOWER, 1)
        );
    }
}
