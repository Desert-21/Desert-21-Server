package com.github.maciejmalewicz.Desert21.dto.game;

import com.github.maciejmalewicz.Desert21.misc.BuildingType;

public record BuildingDto(
        BuildingType type,
        int level
) {
}
