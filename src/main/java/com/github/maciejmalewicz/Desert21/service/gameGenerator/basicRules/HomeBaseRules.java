package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.misc.BuildingType;
import com.github.maciejmalewicz.Desert21.misc.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;

import java.util.Arrays;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.misc.BuildingType.HOME_BASE;

public class HomeBaseRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules() {
        return Arrays.asList(
                new BoardLocationRule(List.of(new Location(9, 1)), HOME_BASE, 1),
                new BoardLocationRule(List.of(new Location(1, 9)), HOME_BASE, 1)
        );
    }
}
