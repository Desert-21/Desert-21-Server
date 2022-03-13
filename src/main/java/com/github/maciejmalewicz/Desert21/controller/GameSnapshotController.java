package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.game.GameDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.gameSnapshot.GameSnapshotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/games/snapshots")
@RestController
public class GameSnapshotController {

    private final GameSnapshotService gameSnapshotService;

    public GameSnapshotController(GameSnapshotService gameSnapshotService) {
        this.gameSnapshotService = gameSnapshotService;
    }

    @RequestMapping("/{gameId}")
    @GetMapping
    public ResponseEntity<GameDto> getGameSnapshot(@PathVariable("gameId") String gameId, Authentication authentication) throws NotAcceptableException {
        var snapshot = gameSnapshotService.snapshotGame(gameId, authentication);
        return ResponseEntity.ok(snapshot);
    }
}
