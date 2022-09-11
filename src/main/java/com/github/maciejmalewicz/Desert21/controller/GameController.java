package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.GameIdResponseDto;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.GameInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/games")
@RestController
public class GameController {

    @Autowired
    private GameInfoService gameInfoService;

    @GetMapping
    public ResponseEntity<GameIdResponseDto> getGameId(Authentication authentication) throws AuthorizationException {
        var id = gameInfoService.getGameIdByUsersAuthentication(authentication);
        var wrapped = new GameIdResponseDto(id);
        return ResponseEntity.ok(wrapped);
    }
}
