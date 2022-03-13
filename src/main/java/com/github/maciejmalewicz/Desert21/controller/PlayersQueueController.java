package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.playersQueue.PlayersQueueService;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/queue")
public class PlayersQueueController {

    private final PlayersQueueService playersQueueService;

    public PlayersQueueController(PlayersQueueService playersQueueService) {
        this.playersQueueService = playersQueueService;
    }

    @PostMapping
    public ResponseEntity<Void> addPlayerToTheQueue(Authentication authentication) throws NotAcceptableException {
        var playersId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                        .orElseThrow(() -> new NotAcceptableException("Could not recognize the player!"));
        playersQueueService.addPlayerToQueue(playersId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/cancel")
    @PostMapping
    public ResponseEntity<Void> cancelPlayerQueue(Authentication authentication) throws NotAcceptableException {
        var playersId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new NotAcceptableException("Could not recognize the player!"));
        playersQueueService.removePlayerFromQueue(playersId);
        return ResponseEntity.ok().build();
    }
}
