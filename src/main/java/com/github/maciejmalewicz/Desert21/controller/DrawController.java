package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.game.DrawAction;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.DrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draw")
public class DrawController {

    @Autowired
    private DrawService drawService;

    @PostMapping("{gameId}")
    public ResponseEntity<Void> request(
            @PathVariable("gameId") String gameId,
            @RequestBody String actionString,
            Authentication authentication) throws NotAcceptableException, AuthorizationException {
        var action = DrawAction.valueOf(actionString);
        drawService.draw(gameId, authentication, action);
        return ResponseEntity.ok().build();
    }
}
