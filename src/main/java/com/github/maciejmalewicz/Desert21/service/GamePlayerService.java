package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.misc.GamePlayerData;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class GamePlayerService {

    private final GameRepository gameRepository;

    public GamePlayerService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GamePlayerData getGamePlayerData(String gameId, Authentication authentication) throws NotAcceptableException {
        var authorities = authentication.getAuthorities();
        var playerId = AuthoritiesUtils.getIdFromAuthorities(authorities)
                .orElseThrow(() -> new NotAcceptableException("Player id could not be extracted!"));
        var game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotAcceptableException("Game not found!"));
        var player = game.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findAny()
                .orElseThrow(() -> new NotAcceptableException("Player not part of the game!"));
        return new GamePlayerData(
                game,
                player
        );
    }

}
