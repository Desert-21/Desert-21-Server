package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersTurnDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games/turns")
public class GameTurnController {

    @PostMapping
    public ResponseEntity<Void> executeTurn(PlayersTurnDto playersTurnDto) {
        return ResponseEntity.ok().build();
    }
}
