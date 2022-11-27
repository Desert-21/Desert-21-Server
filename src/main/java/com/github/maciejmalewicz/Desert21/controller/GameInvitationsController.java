package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.dto.InvitationIdDto;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.GameInvitationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/game-invitations")
public class GameInvitationsController {

    @Autowired
    private GameInvitationsService gameInvitationsService;

    @PostMapping("/invite/{playerId}")
    public ResponseEntity<InvitationIdDto> invitePlayerToGame(@NotNull Authentication authentication, @PathVariable("playerId") String playerId) throws AuthorizationException, NotAcceptableException {
        var invitationId = gameInvitationsService.inviteToGame(authentication, playerId);
        return ResponseEntity.ok(new InvitationIdDto(invitationId));
    }

    @PostMapping("/cancel/{invitationId}")
    public ResponseEntity<Void> cancelInvitation(@NotNull Authentication authentication, @PathVariable("invitationId") String invitationId) throws AuthorizationException, NotAcceptableException {
        gameInvitationsService.cancelInvitation(authentication, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{invitationId}")
    public ResponseEntity<Void> rejectInvitation(@NotNull Authentication authentication, @PathVariable("invitationId") String invitationId) throws AuthorizationException, NotAcceptableException {
        gameInvitationsService.rejectInvitation(authentication, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{invitationId}")
    public ResponseEntity<Void> acceptInvitation(@NotNull Authentication authentication, @PathVariable("invitationId") String invitationId) throws AuthorizationException, NotAcceptableException {
        gameInvitationsService.acceptInvitation(authentication, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm/{invitationId}")
    public ResponseEntity<Void> confirmInvitation(@NotNull Authentication authentication, @PathVariable("invitationId") String invitationId) throws AuthorizationException, NotAcceptableException {
        gameInvitationsService.confirmReadiness(authentication, invitationId);
        return ResponseEntity.ok().build();
    }
}
