package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/balance")
public class GameBalanceController {

   private final GameBalanceService gameBalanceService;

    public GameBalanceController(GameBalanceService gameBalanceService) {
        this.gameBalanceService = gameBalanceService;
    }

    @GetMapping
    public ResponseEntity<GameBalanceDto> getGameBalance() {
        var balance = gameBalanceService.getGameBalance();
        return ResponseEntity.ok(balance);
    }
}
