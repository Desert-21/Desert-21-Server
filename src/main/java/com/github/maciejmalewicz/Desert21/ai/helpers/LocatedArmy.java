package com.github.maciejmalewicz.Desert21.ai.helpers;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.models.Location;

public record LocatedArmy(Location location, Army army) {
}
