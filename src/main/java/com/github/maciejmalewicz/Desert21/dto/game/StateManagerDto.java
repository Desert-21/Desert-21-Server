package com.github.maciejmalewicz.Desert21.dto.game;

import com.github.maciejmalewicz.Desert21.domain.games.GameState;

import java.util.Date;

public record StateManagerDto(
        GameState gameState,
        Date timeout,
        String currentPlayerId,
        int turnCounter
) {
}
