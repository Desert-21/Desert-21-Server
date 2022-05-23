package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.dto.GameIdResponseDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GameInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

//just for testing
@RequestMapping("/games")
@RestController
public class GameController {

    @Autowired
    private GameRepository repository;

    @Autowired
    private GameInfoService gameInfoService;

    @PostMapping
    public void postGame(@RequestBody Game game) {
        repository.save(game);
    }

    @GetMapping
    public ResponseEntity<GameIdResponseDto> getGameId(Authentication authentication) throws NotAcceptableException {
        var id = gameInfoService.getGameIdByUsersAuthentication(authentication);
        var wrapped = new GameIdResponseDto(id);
        return ResponseEntity.ok(wrapped);
    }


}
