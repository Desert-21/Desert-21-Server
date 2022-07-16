package com.github.maciejmalewicz.Desert21.dto.game;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;

import java.util.List;

public record PlayerDto(
        String id,
        String nickname,
        ResourceSet resources,
        List<LabUpgrade> upgrades,
        int rocketStrikesDone,
        int builtFactories,
        int builtTowers
) {
}
