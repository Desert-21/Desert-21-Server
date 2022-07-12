package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.TOWER;

public class BetweenPartsTowersRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        var twoBeforeMiddle = (boardSize / 2) - 2;
        var middle = boardSize / 2;
        var twoAfterMiddle = (boardSize / 2) + 2;
        var end = boardSize - 1;
        return List.of(
                new BoardLocationRule(LocationUtils.generateLocationsSquare(0, twoBeforeMiddle, middle, middle), TOWER, 1),
                new BoardLocationRule(LocationUtils.generateLocationsSquare(twoAfterMiddle, end, middle, middle), TOWER, 1),
                new BoardLocationRule(LocationUtils.generateLocationsSquare(middle, middle, 0, twoBeforeMiddle), TOWER, 1),
                new BoardLocationRule(LocationUtils.generateLocationsSquare(middle, middle, twoAfterMiddle, end), TOWER, 1)
        );
    }
}
