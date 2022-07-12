package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules.*;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@JsonPropertyOrder(alphabetic = true)
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
                .flatMap(ruleSupplier -> ruleSupplier.getRules(getSize()).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Location> getPLayer1Locations() {
        return LocationUtils.generateLocationsSquare(getSize() - 2, getSize() - 1, 0, 1);
    }

    @Override
    public List<Location> getPLayer2Locations() {
        return LocationUtils.generateLocationsSquare(0, 1, getSize() - 2, getSize() - 1);
    }
}
