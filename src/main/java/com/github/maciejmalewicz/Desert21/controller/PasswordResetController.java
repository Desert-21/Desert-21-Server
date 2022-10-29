package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.PasswordResetDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @RequestMapping("/request/{email}")
    @PostMapping
    public ResponseEntity<Void> requestPasswordReset(@PathVariable("email") String email) throws NotAcceptableException {
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/execute")
    @PostMapping
    public ResponseEntity<Void> executePasswordReset(@RequestBody PasswordResetDto resetDto) throws NotAcceptableException {
        passwordResetService.executePasswordReset(resetDto.email(), resetDto.linkId(), resetDto.linkCode(), resetDto.password());
        return ResponseEntity.ok().build();
    }
}
