package com.github.maciejmalewicz.Desert21.controller;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/friends")
public class FriendsController {

    @Autowired
    private FriendsService friendsService;

    @PostMapping("request/{friendId}")
    public ResponseEntity<Void> inviteToFriends(@NotNull Authentication auth, @PathVariable("friendId") String friendId) throws NotAcceptableException {
        friendsService.invitePlayerToFriends(auth, friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("accept/{invitationId}")
    public ResponseEntity<Void> acceptFriendsInvitation(@NotNull Authentication auth, @PathVariable("invitationId") String invitationId) throws NotAcceptableException {
        friendsService.acceptFriendsInvitation(auth, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("reject/{invitationId}")
    public ResponseEntity<Void> rejectFriendsInvitation(@NotNull Authentication auth, @PathVariable("invitationId") String invitationId)  throws NotAcceptableException {
        friendsService.rejectFriendsInvitation(auth, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("remove/{friendId}")
    public ResponseEntity<Void> removeFriend(@NotNull Authentication auth, @PathVariable("friendId") String friendId) throws NotAcceptableException {
        friendsService.removeFriend(auth, friendId);
        return ResponseEntity.ok().build();
    }
}
