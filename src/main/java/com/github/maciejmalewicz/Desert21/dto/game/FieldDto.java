package com.github.maciejmalewicz.Desert21.dto.game;

import com.github.maciejmalewicz.Desert21.domain.games.Army;

public record FieldDto(
        BuildingDto building,
        String ownerId,
        Army army
) {
}
