package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.GameReadinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gameReadiness")
public class GameReadinessController {

    private final GameReadinessService gameReadinessService;

    public GameReadinessController(GameReadinessService gameReadinessService) {
        this.gameReadinessService = gameReadinessService;
    }

    @PostMapping
    public ResponseEntity<Void> notifyAboutReadiness(Authentication authentication, @RequestBody String gameId) throws NotAcceptableException {
        gameReadinessService.notifyAboutReadiness(authentication, gameId);
        return ResponseEntity.ok().build();
    }
}
