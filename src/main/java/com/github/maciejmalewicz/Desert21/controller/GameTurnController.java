package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersTurnDto;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.PlayerTurnService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games/turns")
public class GameTurnController {

    private final PlayerTurnService playerTurnService;

    public GameTurnController(PlayerTurnService playerTurnService) {
        this.playerTurnService = playerTurnService;
    }

    @PostMapping
    public ResponseEntity<Void> executeTurn(Authentication authentication, @RequestBody PlayersTurnDto playersTurnDto)
            throws NotAcceptableException, IllegalArgumentException, AuthorizationException {
        playerTurnService.executeTurn(authentication, playersTurnDto);
        return ResponseEntity.ok().build();
    }
}
