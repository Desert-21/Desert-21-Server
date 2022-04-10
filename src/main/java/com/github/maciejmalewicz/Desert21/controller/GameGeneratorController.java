package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardGeneratorService;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gameGenerator")
public class GameGeneratorController {

    private final BoardGeneratorService generatorService;
    private final GameGeneratorService gameGeneratorService;

    public GameGeneratorController(BoardGeneratorService generatorService, GameGeneratorService gameGeneratorService) {
        this.generatorService = generatorService;
        this.gameGeneratorService = gameGeneratorService;
    }

//    @GetMapping
//    public Game generate() {
//        var generated = gameGeneratorService.generateGame("6223a21ab9efff203d05a29c", "6224829c2eddc5530b04ba0b");
//        return generated;
//    }
}
