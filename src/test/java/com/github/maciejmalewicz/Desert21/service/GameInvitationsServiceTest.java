package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.FriendEntry;
import com.github.maciejmalewicz.Desert21.domain.users.GameInvitation;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameInvitationRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.GameInvitationNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class GameInvitationsServiceTest {

    private GameInvitationsService tested;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameInvitationRepository gameInvitationRepository;

    private PlayersNotifier playersNotifier;
    private GameGeneratorService gameGeneratorService;

    @BeforeEach
    void setup() {
        playersNotifier = mock(PlayersNotifier.class);
        gameGeneratorService = mock(GameGeneratorService.class);
        tested = new GameInvitationsService(
                applicationUserRepository,
                gameRepository,
                gameInvitationRepository,
                playersNotifier,
                gameGeneratorService);
    }

    @Test
    void inviteToGameEmptyAuthentication() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.inviteToGame(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void inviteToGameWrongPlayerIdInAuth() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "other id"))).when(mockAuthentication).getAuthorities();

        applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.inviteToGame(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void inviteToGamePlayerNotWithinFriends() {
        var user1 = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var user2 = applicationUserRepository.save(new ApplicationUser("schabina", new LoginData("schabina@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user1.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.inviteToGame(mockAuthentication, user2.getId());
        });
        assertEquals("Invited player could not be found in the friends list!", exc.getMessage());
    }

    @Test
    void inviteToGameInvitingPlayerInGame() {
        var user1 = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var user2 = applicationUserRepository.save(new ApplicationUser("schabina", new LoginData("schabina@gmail.com", "Password")));
        user1.setFriends(List.of(new FriendEntry(user2.getId(), "schabina")));
        user2.setFriends(List.of(new FriendEntry(user1.getId(), "macior")));
        var savedUser1 = applicationUserRepository.save(user1);
        var savedUser2 = applicationUserRepository.save(user2);

        // user1 in game
        gameRepository.save(new Game(
                List.of(
                        new Player(savedUser1.getId(), "macior", new ResourceSet(100, 100, 100)),
                        new Player("random id", "random player", new ResourceSet(100 ,100, 100))
                ),
                BoardUtils.generateEmptyPlain(2),
                new StateManager(GameState.AWAITING, new Date(Long.MAX_VALUE), user1.getId(), "aaaa")
        ));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + savedUser1.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.inviteToGame(mockAuthentication, savedUser2.getId());
        });
        assertEquals("Player is already in the game and cannot be invited to another one!", exc.getMessage());
    }

    @Test
    void inviteToGameInvitedPlayerInGame() {
        var user1 = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var user2 = applicationUserRepository.save(new ApplicationUser("schabina", new LoginData("schabina@gmail.com", "Password")));
        user1.setFriends(List.of(new FriendEntry(user2.getId(), "schabina")));
        user2.setFriends(List.of(new FriendEntry(user1.getId(), "macior")));
        var savedUser1 = applicationUserRepository.save(user1);
        var savedUser2 = applicationUserRepository.save(user2);

        // user2 in game
        gameRepository.save(new Game(
                List.of(
                        new Player(savedUser2.getId(), "macior", new ResourceSet(100, 100, 100)),
                        new Player("random id", "random player", new ResourceSet(100 ,100, 100))
                ),
                BoardUtils.generateEmptyPlain(2),
                new StateManager(GameState.AWAITING, new Date(Long.MAX_VALUE), user1.getId(), "aaaa")
        ));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + savedUser1.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.inviteToGame(mockAuthentication, savedUser2.getId());
        });
        assertEquals("Player is already in the game and cannot be invited to another one!", exc.getMessage());
    }

    @Test
    void inviteToGameHappyPath() throws AuthorizationException, NotAcceptableException{
        var user1 = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var user2 = applicationUserRepository.save(new ApplicationUser("schabina", new LoginData("schabina@gmail.com", "Password")));
        user1.setFriends(List.of(new FriendEntry(user2.getId(), "schabina")));
        user2.setFriends(List.of(new FriendEntry(user1.getId(), "macior")));
        var savedUser1 = applicationUserRepository.save(user1);
        var savedUser2 = applicationUserRepository.save(user2);

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + savedUser1.getId()))).when(mockAuthentication).getAuthorities();

        var invitationId = tested.inviteToGame(mockAuthentication, savedUser2.getId());

        var invitation = gameInvitationRepository.findAll().stream()
                .findFirst()
                .orElseThrow();
        assertEquals(invitation.getId(), invitationId);
        assertNotNull(invitation.getExpiryDate());
        assertEquals(user1.getId(), invitation.getRequestedBy());
        assertEquals(user2.getId(), invitation.getRequestedFriend());
        assertFalse(invitation.isAccepted());

        verify(playersNotifier, times(1)).notifyPlayer(user2.getId(), new Notification<>(
                GAME_INVITATION_RECEIVED_NOTIFICATION,
                new GameInvitationNotification(invitation.getId(), "macior")
        ));
    }

    @Test
    void cancelInvitationEmptyAuthentication() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.cancelInvitation(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void cancelInvitationWrongPlayerIdInAuth() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "other id"))).when(mockAuthentication).getAuthorities();

        applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.cancelInvitation(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void cancelInvitationInvitationNotFound() {
        gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.cancelInvitation(mockAuthentication, "non existing");
        });
        assertEquals("Game invitation not found!", exc.getMessage());
    }

    @Test
    void cancelInvitationOtherPlayerInviting() {
        var invitation = gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.cancelInvitation(mockAuthentication, invitation.getId());
        });
        assertEquals("Invitation has been requested by another player! Cannot cancel it!", exc.getMessage());
    }

    @Test
    void cancelInvitationHappyPath() throws AuthorizationException, NotAcceptableException {
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var invitation = gameInvitationRepository.save(new GameInvitation(user.getId(), "other player"));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        tested.cancelInvitation(mockAuthentication, invitation.getId());

        assertTrue(gameInvitationRepository.findById(invitation.getId()).isEmpty());
        verify(playersNotifier, times(1)).notifyPlayer("other player", new Notification<>(
                GAME_INVITATION_CANCELLED_NOTIFICATION,
                new GameInvitationNotification(invitation.getId(), user.getNickname())
        ));
    }

    @Test
    void rejectInvitationEmptyAuthentication() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.rejectInvitation(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void rejectInvitationWrongPlayerIdInAuth() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "other id"))).when(mockAuthentication).getAuthorities();

        applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.rejectInvitation(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void rejectInvitationInvitationNotFound() {
        gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.rejectInvitation(mockAuthentication, "non existing");
        });
        assertEquals("Game invitation not found!", exc.getMessage());
    }

    @Test
    void rejectInvitationOtherPlayerInvited() {
        var invitation = gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.rejectInvitation(mockAuthentication, invitation.getId());
        });
        assertEquals("Game has been requested for another player! Cannot reject invitation!", exc.getMessage());
    }

    @Test
    void rejectInvitationHappyPath() throws AuthorizationException, NotAcceptableException {
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var invitation = gameInvitationRepository.save(new GameInvitation("other player", user.getId()));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        tested.rejectInvitation(mockAuthentication, invitation.getId());

        verify(playersNotifier, times(1)).notifyPlayer("other player", new Notification<>(
                GAME_INVITATION_REJECTED_NOTIFICATION,
                new GameInvitationNotification(invitation.getId(), user.getNickname())
        ));
    }

    @Test
    void acceptInvitationEmptyAuthentication() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.acceptInvitation(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void acceptInvitationWrongPlayerIdInAuth() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "other id"))).when(mockAuthentication).getAuthorities();

        applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.acceptInvitation(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void acceptInvitationInvitationNotFound() {
        gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptInvitation(mockAuthentication, "non existing");
        });
        assertEquals("Game invitation not found!", exc.getMessage());
    }

    @Test
    void acceptInvitationOtherPlayerInvited() {
        var invitation = gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.acceptInvitation(mockAuthentication, invitation.getId());
        });
        assertEquals("Game has been requested for another player! Cannot accept invitation!", exc.getMessage());
    }

    @Test
    void acceptInvitationHappyPath() throws AuthorizationException, NotAcceptableException {
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var invitation = gameInvitationRepository.save(new GameInvitation("other player", user.getId()));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        tested.acceptInvitation(mockAuthentication, invitation.getId());

        var savedInvitation = gameInvitationRepository.findById(invitation.getId()).orElseThrow();
        assertTrue(savedInvitation.isAccepted());

        verify(playersNotifier, times(1)).notifyPlayer("other player", new Notification<>(
                GAME_INVITATION_ACCEPTED_NOTIFICATION,
                new GameInvitationNotification(invitation.getId(), user.getNickname())
        ));
    }

    @Test
    void confirmReadinessEmptyAuthentication() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.confirmReadiness(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void confirmReadinessWrongPlayerIdInAuth() {
        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + "other id"))).when(mockAuthentication).getAuthorities();

        applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.confirmReadiness(mockAuthentication, "ignored");
        });
        assertEquals("Player could not be identified!", exc.getMessage());
    }

    @Test
    void confirmReadinessInvitationNotFound() {
        gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.confirmReadiness(mockAuthentication, "non existing");
        });
        assertEquals("Game invitation not found!", exc.getMessage());
    }

    @Test
    void confirmReadinessOtherPlayerInviting() {
        var invitation = gameInvitationRepository.save(new GameInvitation("other1", "other2"));
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.confirmReadiness(mockAuthentication, invitation.getId());
        });
        assertEquals("Game has been requested by another player! Cannot confirm readiness!", exc.getMessage());
    }

    @Test
    void confirmReadinessNotYetAccepted() {
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var invitation = gameInvitationRepository.save(new GameInvitation(user.getId(), "other2"));

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        var exc = assertThrows(NotAcceptableException.class, () -> {
            tested.confirmReadiness(mockAuthentication, invitation.getId());
        });
        assertEquals("Invitation wasn't accepted yet! You can't confirm readiness before it's accepted.", exc.getMessage());
    }

    @Test
    void confirmReadinessHappyPath() throws AuthorizationException, NotAcceptableException {
        var user = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
        var invitation = gameInvitationRepository.save(new GameInvitation(user.getId(), "other2"));
        invitation.setAccepted(true);
        var acceptedInvitation = gameInvitationRepository.save(invitation);

        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(mockAuthentication).getAuthorities();

        tested.confirmReadiness(mockAuthentication, acceptedInvitation.getId());

        verify(gameGeneratorService, times(1)).generateGame(invitation.getRequestedFriend(), invitation.getRequestedBy());
        assertTrue(gameInvitationRepository.findById(acceptedInvitation.getId()).isEmpty());
    }
}