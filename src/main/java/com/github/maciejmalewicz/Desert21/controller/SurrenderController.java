package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.SurrenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/surrender")
public class SurrenderController {

    @Autowired
    private SurrenderService service;

    @PostMapping("/{gameId}")
    public ResponseEntity<Void> surrender(Authentication auth, @PathVariable("gameId") String gameId) throws NotAcceptableException {
        service.surrender(auth, gameId);
        return ResponseEntity.ok().build();
    }
}
