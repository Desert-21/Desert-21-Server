package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class GameInfoService {

    private final GameRepository gameRepository;

    public GameInfoService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public String getGameIdByUsersAuthentication(Authentication authentication) throws AuthorizationException {
        if (authentication == null) {
            throw new AuthorizationException("User could not be recognized!");
        }
        var usersId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new AuthorizationException("User could not be recognized!"));
        return gameRepository.findByPlayersId(usersId)
                .filter(g -> g.getStateManager().getGameState() != GameState.FINISHED)
                .map(Game::getId)
                .orElse(null);
    }
}
