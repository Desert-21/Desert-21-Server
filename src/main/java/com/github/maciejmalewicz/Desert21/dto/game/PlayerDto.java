package com.github.maciejmalewicz.Desert21.dto.game;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;

public record PlayerDto(
        String id,
        String nickname,
        ResourceSet resources
) {
}
