package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/AI/play")
public class PlayAgainstAIController {

    @Autowired
    private GameGeneratorService gameGeneratorService;

    @PostMapping
    public ResponseEntity<Void> playAgainstAI(Authentication auth) throws AuthorizationException {
        gameGeneratorService.generateGameAgainstAI(auth);
        return ResponseEntity.ok().build();
    }
}
