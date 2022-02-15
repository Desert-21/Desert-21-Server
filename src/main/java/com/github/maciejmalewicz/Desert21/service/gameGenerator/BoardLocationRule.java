package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.misc.BuildingType;
import com.github.maciejmalewicz.Desert21.misc.Location;

import java.util.List;
import java.util.Set;

public record BoardLocationRule(List<Location> availableLocations, BuildingType buildingType, int amount) {

}
