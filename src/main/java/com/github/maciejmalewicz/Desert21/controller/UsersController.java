package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.UsersData;
import com.github.maciejmalewicz.Desert21.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public ResponseEntity<UsersData> getUserFromAuth(@NotNull Authentication auth) throws AuthorizationException {
        if (auth == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var email = auth.getName();
        var data = usersService.getUsersData(email);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/ping/{userId}")
    public ResponseEntity<Void> requestUsersStatusPing(@NotNull Authentication authentication, @PathVariable("userId") String userId) throws AuthorizationException {
        usersService.requestUsersStatusPing(authentication, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping/all")
    public ResponseEntity<Void> requestAllUsersStatusPing(@NotNull Authentication authentication, @RequestBody List<String> userIds) throws AuthorizationException {
        usersService.requestAllUsersPing(authentication, userIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping/active/{playersId}")
    public ResponseEntity<Void> pingUsersStatus(@NotNull Authentication authentication, @PathVariable("playersId") String requestingPlayersId) throws AuthorizationException {
        usersService.pingActivity(authentication, requestingPlayersId);
        return ResponseEntity.ok().build();
    }
}
