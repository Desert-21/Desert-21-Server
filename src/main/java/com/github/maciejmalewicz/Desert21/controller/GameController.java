package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//just for testing
@RequestMapping("/games")
@RestController
public class GameController {

    @Autowired
    private GameRepository repository;

    @PostMapping
    public void postGame(@RequestBody Game game) {
        repository.save(game);
    }
}
