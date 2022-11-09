package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.FriendEntry;
import com.github.maciejmalewicz.Desert21.domain.users.FriendsInvitation;
import com.github.maciejmalewicz.Desert21.dto.FriendsInvitationDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.FriendsInvitationRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;

@Service
public class FriendsService {

    private final FriendsInvitationRepository friendsInvitationRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final PlayersNotifier playersNotifier;

    public FriendsService(FriendsInvitationRepository friendsInvitationRepository, ApplicationUserRepository applicationUserRepository, PlayersNotifier playersNotifier) {
        this.friendsInvitationRepository = friendsInvitationRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.playersNotifier = playersNotifier;
    }

    @Transactional
    public void invitePlayerToFriends(Authentication authentication, String friendNickname) throws NotAcceptableException {
        var invitingId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new NotAcceptableException("Could not recognize you! Are you sure you are logged in?"));
        var invitedFriend = applicationUserRepository.findFirstByNickname(friendNickname)
                .orElseThrow(() -> new NotAcceptableException("Could not find any player with that nickname! Make sure you haven't made any typo!"));
        var invitingPlayer = applicationUserRepository.findById(invitingId)
                .orElseThrow(() -> new NotAcceptableException("How about you stop trying to hack it this time?"));

        if (invitedFriend.getId().equals(invitingId)) {
            throw new NotAcceptableException("You can't add yourself as a friend!");
        }

        if (hasAlreadyFriendOfName(invitingPlayer, invitedFriend.getId()) || hasAlreadyFriendOfName(invitedFriend, invitingId)) {
            throw new NotAcceptableException("Could not send a friend request - you are already friends!");
        }

        var invitingPlayersNickname = invitingPlayer.getNickname();

        var invitation = new FriendsInvitation(invitingId, invitedFriend.getId());
        friendsInvitationRepository.save(invitation);

        playersNotifier.notifyPlayer(invitedFriend.getId(), new Notification<>(
                FRIENDS_INVITATION_RECEIVED_NOTIFICATION,
                new FriendsInvitationDto(invitation.getId(), invitingPlayersNickname)
        ));
    }

    @Transactional
    public void acceptFriendsInvitation(Authentication authentication, String invitationId) throws NotAcceptableException {
        var invitedId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new NotAcceptableException("Could not recognize you! Are you sure you are logged in?"));
        var invitedPlayer = applicationUserRepository.findById(invitedId)
                .orElseThrow(() -> new NotAcceptableException("How about you stop trying to hack it this time?"));
        var invitation = friendsInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotAcceptableException("Invitation not found!"));
        if (!invitation.getRequestedFriend().equals(invitedId)) {
            throw new NotAcceptableException("No actions available for this invitation!");
        }
        var invitingPlayer = applicationUserRepository.findById(invitation.getRequestedBy())
                .orElseThrow(() -> new NotAcceptableException("Could not find the player that invited you!"));

        if (hasAlreadyFriendOfName(invitingPlayer, invitedPlayer.getId()) || hasAlreadyFriendOfName(invitedPlayer, invitingPlayer.getId())) {
            throw new NotAcceptableException("Could not accept a friend request - you are already friends!");
        }

        invitedPlayer.getFriends().add(new FriendEntry(invitingPlayer.getId(), invitingPlayer.getNickname()));
        invitingPlayer.getFriends().add(new FriendEntry(invitedPlayer.getId(), invitedPlayer.getNickname()));

        applicationUserRepository.save(invitedPlayer);
        applicationUserRepository.save(invitingPlayer);
        friendsInvitationRepository.delete(invitation);

        playersNotifier.notifyPlayer(invitedPlayer.getId(), new Notification<>(
                FRIENDS_LIST_UPDATED_NOTIFICATION,
                invitedPlayer.getFriends()
        ));

        playersNotifier.notifyPlayer(invitingPlayer.getId(), new Notification<>(
                FRIENDS_LIST_UPDATED_NOTIFICATION,
                invitingPlayer.getFriends()
        ));
        playersNotifier.notifyPlayer(invitingPlayer.getId(), new Notification<>(
                FRIENDS_INVITATION_ACCEPTED_NOTIFICATION,
                invitedPlayer.getNickname()
        ));
    }

    @Transactional
    public void rejectFriendsInvitation(Authentication authentication, String invitationId) throws NotAcceptableException {
        var invitedId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new NotAcceptableException("Could not recognize you! Are you sure you are logged in?"));
        var invitedPlayer = applicationUserRepository.findById(invitedId)
                .orElseThrow(() -> new NotAcceptableException("How about you stop trying to hack it this time?"));
        var invitation = friendsInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotAcceptableException("Invitation not found!"));
        if (!invitation.getRequestedFriend().equals(invitedId)) {
            throw new NotAcceptableException("No actions available for this invitation!");
        }

        friendsInvitationRepository.delete(invitation);

        playersNotifier.notifyPlayer(invitation.getRequestedBy(), new Notification<>(
                FRIENDS_INVITATION_REJECTED_NOTIFICATION,
                invitedPlayer.getNickname()
        ));
    }

    @Transactional
    public void removeFriend(Authentication authentication, String friendId) throws NotAcceptableException {
        var userId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new NotAcceptableException("Could not recognize you! Are you sure you are logged in?"));
        var removingUser = applicationUserRepository.findById(userId)
                .orElseThrow(() -> new NotAcceptableException("How about you stop trying to hack it this time?"));
        var removedUser = applicationUserRepository.findById(friendId)
                        .orElseThrow(() -> new NotAcceptableException("Could not find a user you are trying to remove from friends!"));

        removingUser.setFriends(
                removingUser.getFriends().stream()
                        .filter(f -> !f.getPlayerId().equals(removedUser.getId()))
                        .collect(Collectors.toList())
        );
        removedUser.setFriends(
                removedUser.getFriends().stream()
                        .filter(f -> !f.getPlayerId().equals(removingUser.getId()))
                        .collect(Collectors.toList())
        );

        applicationUserRepository.save(removingUser);
        applicationUserRepository.save(removedUser);

        playersNotifier.notifyPlayer(removedUser.getId(), new Notification<>(
                REMOVED_FROM_FRIEND_LIST_NOTIFICATION,
                removingUser.getNickname()
        ));
    }

    private boolean hasAlreadyFriendOfName(ApplicationUser user, String friendId) {
        return user.getFriends().stream()
                .anyMatch(f -> f.getPlayerId().equals(friendId));
    }
}
