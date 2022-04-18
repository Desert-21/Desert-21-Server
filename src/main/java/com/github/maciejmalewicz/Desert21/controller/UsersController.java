package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.UsersData;
import com.github.maciejmalewicz.Desert21.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public ResponseEntity<UsersData> getUserFromAuth(Authentication auth) throws NotAcceptableException {
        if (auth == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var email = auth.getName();
        var data = usersService.getUsersData(email);
        return ResponseEntity.ok(data);
    }
}
