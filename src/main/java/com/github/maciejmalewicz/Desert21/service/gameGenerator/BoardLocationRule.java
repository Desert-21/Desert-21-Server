package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.misc.BuildingType;
import com.github.maciejmalewicz.Desert21.misc.Location;

import java.util.List;

public record BoardLocationRule(List<Location> availableLocations, BuildingType buildingType, int amount) {

}
