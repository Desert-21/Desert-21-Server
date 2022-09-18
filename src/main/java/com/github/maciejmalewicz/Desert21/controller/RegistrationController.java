package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.AccountActivationDto;
import com.github.maciejmalewicz.Desert21.dto.RegistrationDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    @PostMapping
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegistrationDto dto) throws NotAcceptableException {
        registrationService.registerUser(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @RequestMapping("activation")
    public ResponseEntity<Void> activateAccount(@RequestBody AccountActivationDto dto) throws NotAcceptableException {
        registrationService.activateAccount(dto);
        return ResponseEntity.ok().build();
    }
}
