package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;

import java.util.List;

public record BoardLocationRule(List<Location> availableLocations, BuildingType buildingType, int amount) {

}
