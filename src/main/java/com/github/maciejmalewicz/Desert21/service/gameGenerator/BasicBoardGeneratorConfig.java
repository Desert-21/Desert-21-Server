package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.misc.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules.*;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BasicBoardGeneratorConfig implements BoardGeneratorConfig {

    @Override
    public int getSize() {
        return 11;
    }

    @Override
    public List<BoardLocationRule> getBoardLocationRules() {
        return Stream.of(
                new HomeBaseRules(),
                new PlayersFactoriesRules(),
                new RocketLaunchersRules(),
                new CornerFactoriesRules(),
                new BetweenPartsTowersRules(),
                new PartialSquaresRules()
        )
                .flatMap(ruleSupplier -> ruleSupplier.getRules().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Location> getPLayer1Locations() {
        return LocationUtils.generateLocationsSquare(9, 10, 0, 1);
    }

    @Override
    public List<Location> getPLayer2Locations() {
        return LocationUtils.generateLocationsSquare(0, 1, 9, 10);
    }
}
