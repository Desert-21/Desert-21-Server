package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.misc.BuildingType.TOWER;

public class BetweenPartsTowersRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules() {
        return List.of(
                new BoardLocationRule(LocationUtils.generateLocationsSquare(0, 3, 5, 5), TOWER, 1),
                new BoardLocationRule(LocationUtils.generateLocationsSquare(7, 10, 5, 5), TOWER, 1),
                new BoardLocationRule(LocationUtils.generateLocationsSquare(5, 5, 0, 3), TOWER, 1),
                new BoardLocationRule(LocationUtils.generateLocationsSquare(5, 5, 7, 10), TOWER, 1)
        );
    }
}
