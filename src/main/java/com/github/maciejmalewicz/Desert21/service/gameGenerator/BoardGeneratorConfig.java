package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.models.Location;

import java.util.List;

public interface BoardGeneratorConfig {
    int getSize();
    List<BoardLocationRule> getBoardLocationRules();
    List<Location> getPLayer1Locations();
    List<Location> getPLayer2Locations();
}
