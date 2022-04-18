package com.github.maciejmalewicz.Desert21.dto.game;

import com.github.maciejmalewicz.Desert21.models.BuildingType;

public record BuildingDto(
        BuildingType type,
        int level
) {
}
