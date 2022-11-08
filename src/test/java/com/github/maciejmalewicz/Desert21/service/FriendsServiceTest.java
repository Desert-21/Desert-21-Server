package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.FriendEntry;
import com.github.maciejmalewicz.Desert21.domain.users.FriendsInvitation;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.dto.FriendsInvitationDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.FriendsInvitationRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class FriendsServiceTest {

    private FriendsService tested;
    private PlayersNotifier playersNotifier;

    private ApplicationUser inviting;
    private ApplicationUser invited;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private FriendsInvitationRepository friendsInvitationRepository;

    private Authentication authentication;

    @BeforeEach
    void setup() {
        playersNotifier = mock(PlayersNotifier.class);
        tested = new FriendsService(friendsInvitationRepository, applicationUserRepository, playersNotifier);

        applicationUserRepository.deleteAll();
    }

    void setupPlayers() {
        inviting = applicationUserRepository.save(new ApplicationUser(
                "macior",
                new LoginData("macior@email.com", "Password")
        ));
        invited = applicationUserRepository.save(new ApplicationUser(
                "schabina",
                new LoginData("schabina@email.com", "Password")
        ));

        inviting.setFriends(List.of(new FriendEntry("AAA", "other_player")));
        invited.setFriends(List.of(new FriendEntry("AAA", "other_player")));

        inviting = applicationUserRepository.save(inviting);
        invited = applicationUserRepository.save(invited);
    }

    void setupPlayersFriends() {
        inviting = applicationUserRepository.save(new ApplicationUser(
                "macior",
                new LoginData("macior@email.com", "Password")
        ));
        invited = applicationUserRepository.save(new ApplicationUser(
                "schabina",
                new LoginData("schabina@email.com", "Password")
        ));

        inviting.setFriends(List.of(
                new FriendEntry("AAA", "other_player"),
                new FriendEntry(invited.getId(), "schabina")
                ));
        invited.setFriends(List.of(
                new FriendEntry("AAA", "other_player"),
                new FriendEntry(inviting.getId(), "macior")
                ));

        inviting = applicationUserRepository.save(inviting);
        invited = applicationUserRepository.save(invited);
    }

    void setupAuthForInviting() {
        authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + inviting.getId()))).when(authentication).getAuthorities();
    }

    void setupAuthForInvited() {
        authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + invited.getId()))).when(authentication).getAuthorities();
    }

    @Test
    void invitePlayerToFriendsAuthNoId() {
        setupPlayers();
        authentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.invitePlayerToFriends(authentication, "schabina");
        });
        assertEquals("Could not recognize you! Are you sure you are logged in?", exc.getMessage());
    }

    @Test
    void invitePlayerToFriendsPlayerNotFound() {
        setupPlayers();
        setupAuthForInviting();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.invitePlayerToFriends(authentication, "non existing player");
        });
        assertEquals("Could not find any player with that nickname! Make sure you haven't made any typo!", exc.getMessage());
    }

    @Test
    void invitePlayerToFriendsInviterIdNotFound() {
        setupPlayers();
        authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "non_existing"))).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.invitePlayerToFriends(authentication, "schabina");
        });
        assertEquals("How about you stop trying to hack it this time?", exc.getMessage());
    }

    @Test
    void invitePlayerToFriendsInvitingHimself() {
        setupPlayers();
        authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + inviting.getId()))).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.invitePlayerToFriends(authentication, inviting.getNickname());
        });
        assertEquals("You can't add yourself as a friend!", exc.getMessage());
    }

    @Test
    void invitePlayerToFriendsInviterAlreadyContainsInvited() {
        setupPlayers();
        setupAuthForInviting();

        var inviter = applicationUserRepository.findById(inviting.getId())
                .orElseThrow();
        inviter.getFriends().add(new FriendEntry(invited.getId(), "schabina"));
        inviting = applicationUserRepository.save(inviter);

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.invitePlayerToFriends(authentication, "schabina");
        });
        assertEquals("Could not send a friend request - you are already friends!", exc.getMessage());
    }

    @Test
    void invitePlayerToFriendsInvitedAlreadyContainsInviter() {
        setupPlayers();
        setupAuthForInviting();

        var invitedFromRepo = applicationUserRepository.findById(invited.getId())
                .orElseThrow();
        invitedFromRepo.getFriends().add(new FriendEntry(inviting.getId(), "macior"));
        invited = applicationUserRepository.save(invitedFromRepo);

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.invitePlayerToFriends(authentication, "schabina");
        });
        assertEquals("Could not send a friend request - you are already friends!", exc.getMessage());
    }

    @Test
    void invitePlayerToFriendsHappyPath() throws NotAcceptableException {
        setupPlayers();
        setupAuthForInviting();

        tested.invitePlayerToFriends(authentication, "schabina");

        var invitation = friendsInvitationRepository.findAll().stream().findFirst().orElseThrow();
        assertEquals(inviting.getId(), invitation.getRequestedBy());
        assertEquals(invited.getId(), invitation.getRequestedFriend());
        assertNotNull(invitation.getExpiryDate());

        verify(playersNotifier, times(1)).notifyPlayer(invited.getId(), new Notification<>(
                FRIENDS_INVITATION_RECEIVED_NOTIFICATION,
                new FriendsInvitationDto(invitation.getId(),inviting.getNickname())
        ));
    }

    @Test
    void acceptFriendsInvitationNoAuthorityInAuth() {
        setupPlayers();
        authentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptFriendsInvitation(authentication, "ignored");
        });
        assertEquals("Could not recognize you! Are you sure you are logged in?", exc.getMessage());
    }

    @Test
    void acceptFriendsInvitationNonExistingUserInAuth() {
        setupPlayers();
        authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "non_existing"))).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptFriendsInvitation(authentication, "ignored");
        });
        assertEquals("How about you stop trying to hack it this time?", exc.getMessage());
    }

    @Test
    void acceptFriendsInvitationNonExistingInvitation() {
        friendsInvitationRepository.deleteAll();
        setupPlayers();
        setupAuthForInvited();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptFriendsInvitation(authentication, "non existing");
        });
        assertEquals("Invitation not found!", exc.getMessage());
    }

    @Test
    void acceptFriendsInvitationOtherPlayerInvited() {
        setupPlayers();
        setupAuthForInvited();
        var invitation = friendsInvitationRepository.save(new FriendsInvitation(
                inviting.getId(),
                "other player"
        ));

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptFriendsInvitation(authentication, invitation.getId());
        });
        assertEquals("No actions available for this invitation!", exc.getMessage());
    }

    @Test
    void acceptFriendsInvitationInviterNotFound() {
        setupPlayers();
        setupAuthForInvited();
        var invitation = friendsInvitationRepository.save(new FriendsInvitation(
                "non existing player",
                invited.getId()
        ));

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptFriendsInvitation(authentication, invitation.getId());
        });
        assertEquals("Could not find the player that invited you!", exc.getMessage());
    }

    @Test
    void acceptFriendsInvitationInviterAlreadyInInvitedFriends() {
        setupPlayers();
        setupAuthForInvited();

        var invitedFromRepo = applicationUserRepository.findById(invited.getId())
                .orElseThrow();
        invitedFromRepo.getFriends().add(new FriendEntry(inviting.getId(), "macior"));
        invited = applicationUserRepository.save(invitedFromRepo);

        var invitation = friendsInvitationRepository.save(new FriendsInvitation(
                inviting.getId(),
                invited.getId()
        ));

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptFriendsInvitation(authentication, invitation.getId());
        });
        assertEquals("Could not accept a friend request - you are already friends!", exc.getMessage());
    }

    @Test
    void acceptFriendsInvitationInvitedAlreadyInInvitersFriends() {
        setupPlayers();
        setupAuthForInvited();

        var inviterFromRepo = applicationUserRepository.findById(invited.getId())
                .orElseThrow();
        inviterFromRepo.getFriends().add(new FriendEntry(invited.getId(), "schabina"));
        inviting = applicationUserRepository.save(inviterFromRepo);

        var invitation = friendsInvitationRepository.save(new FriendsInvitation(
                inviting.getId(),
                invited.getId()
        ));

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptFriendsInvitation(authentication, invitation.getId());
        });
        assertEquals("Could not accept a friend request - you are already friends!", exc.getMessage());
    }

    @Test
    void acceptFriendsInvitationHappyPath() throws NotAcceptableException {
        setupPlayers();
        setupAuthForInvited();

        var invitation = friendsInvitationRepository.save(new FriendsInvitation(
                inviting.getId(),
                invited.getId()
        ));

        tested.acceptFriendsInvitation(authentication, invitation.getId());

        assertEquals(0, friendsInvitationRepository.findAll().size());

        var invitingPlayersFriends = List.of(
                new FriendEntry("AAA", "other_player"),
                new FriendEntry(invited.getId(), invited.getNickname())
        );
        var invitedPlayersFriends = List.of(
                new FriendEntry("AAA", "other_player"),
                new FriendEntry(inviting.getId(), inviting.getNickname())
        );

        var invitingFromRepo = applicationUserRepository.findById(inviting.getId())
                        .orElseThrow();
        assertEquals(invitingPlayersFriends, invitingFromRepo.getFriends());

        var invitedFromRepo = applicationUserRepository.findById(invited.getId())
                .orElseThrow();
        assertEquals(invitedPlayersFriends, invitedFromRepo.getFriends()
        );

        verify(playersNotifier, times(1)).notifyPlayer(invited.getId(), new Notification<>(
                FRIENDS_LIST_UPDATED_NOTIFICATION,
                invitedPlayersFriends
        ));

        verify(playersNotifier, times(1)).notifyPlayer(inviting.getId(), new Notification<>(
                FRIENDS_LIST_UPDATED_NOTIFICATION,
                invitingPlayersFriends
        ));

        verify(playersNotifier, times(1)).notifyPlayer(inviting.getId(), new Notification<>(
                FRIENDS_INVITATION_ACCEPTED_NOTIFICATION,
                invited.getNickname()
        ));
    }

    @Test
    void rejectFriendsInvitationNoIdAuthority() {
        setupPlayers();
        authentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.rejectFriendsInvitation(authentication, "ignored");
        });
        assertEquals("Could not recognize you! Are you sure you are logged in?", exc.getMessage());
    }

    @Test
    void rejectFriendsInvitationNonExistingUserInAuthId() {
        setupPlayers();
        authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "non_existing"))).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.rejectFriendsInvitation(authentication, "ignored");
        });
        assertEquals("How about you stop trying to hack it this time?", exc.getMessage());
    }

    @Test
    void rejectFriendsInvitationNotFoundInvitation() {
        friendsInvitationRepository.deleteAll();
        setupPlayers();
        setupAuthForInvited();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.rejectFriendsInvitation(authentication, "ignored");
        });
        assertEquals("Invitation not found!", exc.getMessage());
    }

    @Test
    void rejectFriendsInvitationOtherPlayersInvitation() {
        setupPlayers();
        setupAuthForInvited();
        var invitation = friendsInvitationRepository.save(new FriendsInvitation(
                inviting.getId(),
                "other player"
        ));

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.rejectFriendsInvitation(authentication, invitation.getId());
        });
        assertEquals("No actions available for this invitation!", exc.getMessage());
    }

    @Test
    void rejectFriendsInvitationHappyPath() throws NotAcceptableException {
        setupPlayers();
        setupAuthForInvited();
        var invitation = friendsInvitationRepository.save(new FriendsInvitation(
                inviting.getId(),
                invited.getId()
        ));

        tested.rejectFriendsInvitation(authentication, invitation.getId());

        assertEquals(0, friendsInvitationRepository.findAll().size());
        verify(playersNotifier, times(1)).notifyPlayer(inviting.getId(), new Notification<>(
                FRIENDS_INVITATION_REJECTED_NOTIFICATION,
                invited.getNickname()
        ));
    }

    @Test
    void removeFriendEmptyAuthorities() {
        setupPlayersFriends();
        authentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.removeFriend(authentication, invited.getId());
        });
        assertEquals("Could not recognize you! Are you sure you are logged in?", exc.getMessage());
    }

    @Test
    void removeFriendNonExistingUserInAuthId() {
        setupPlayersFriends();
        authentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();

        authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "non_existing"))).when(authentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.removeFriend(authentication, invited.getId());
        });
        assertEquals("How about you stop trying to hack it this time?", exc.getMessage());
    }

    @Test
    void removeFriendPlayerNotFound() {
        setupPlayersFriends();
        setupAuthForInviting();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.removeFriend(authentication, "non existing player id");
        });
        assertEquals("Could not find a user you are trying to remove from friends!", exc.getMessage());
    }

    @Test
    void removeFriendHappyPath() throws NotAcceptableException {
        setupPlayersFriends();
        setupAuthForInviting();

        tested.removeFriend(authentication, invited.getId());

        var expectedFriends = List.of(new FriendEntry("AAA", "other_player"));
        var invitingFromRepo = applicationUserRepository.findById(inviting.getId())
                .orElseThrow();
        var invitedFromRepo = applicationUserRepository.findById(invited.getId())
                .orElseThrow();
        assertEquals(expectedFriends, invitingFromRepo.getFriends());
        assertEquals(expectedFriends, invitedFromRepo.getFriends());

        verify(playersNotifier, times(1)).notifyPlayer(invitedFromRepo.getId(), new Notification<>(
                REMOVED_FROM_FRIEND_LIST_NOTIFICATION,
                inviting.getNickname()
        ));
    }
}