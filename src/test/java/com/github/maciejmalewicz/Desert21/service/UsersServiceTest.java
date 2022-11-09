package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class UsersServiceTest {

    private UsersService tested;

    private PlayersNotifier playersNotifier;

    @Autowired
    private ApplicationUserRepository repository;

    @Autowired
    private GameInfoService gameInfoService;

    private ApplicationUser user;

    @BeforeEach
    void setup() {
        playersNotifier = mock(PlayersNotifier.class);
        gameInfoService = spy(gameInfoService);
        tested = new UsersService(repository, gameInfoService, playersNotifier);
        user = repository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password")));
    }

    @Test
    void getUsersDataHappyPath() throws AuthorizationException {
        var user = new ApplicationUser(
                "macior123456",
                new LoginData("m@gmail.com", "Password1")
        );
        user = repository.save(user);
        var retrieved = tested.getUsersData("m@gmail.com");
        assertEquals(user.getId(), retrieved.id());
        assertEquals("macior123456", retrieved.nickname());
    }

    @Test
    void getUsersDataNotFound() {
        var exception = assertThrows(AuthorizationException.class, () -> {
            tested.getUsersData("m@gmail.com");
        });
        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    void requestUsersPingStatusWrongAuthentication() {
        var authentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();
        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.requestUsersStatusPing(authentication, "ignored");
        });
        assertEquals("Could not recognize player!", exc.getMessage());
    }

    @Test
    void requestUsersPingStatusIsAvailable() throws AuthorizationException {
        var authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(authentication).getAuthorities();

        tested.requestUsersStatusPing(authentication, "other_players_id");

        verify(playersNotifier, times(1)).notifyPlayer("other_players_id", new Notification<>(PING_REQUESTED_NOTIFICATION, user.getId()));
    }

    @Test
    void requestUsersPingStatusIsInTheGame() throws AuthorizationException {
        var authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(authentication).getAuthorities();
        doReturn(Optional.of("some_not_null_id")).when(gameInfoService).getGameIdByUsersId("other_players_id");

        tested.requestUsersStatusPing(authentication, "other_players_id");

        verify(playersNotifier, times(1)).notifyPlayer(user.getId(), new Notification<>(PLAYER_IN_GAME_NOTIFICATION, "other_players_id"));
    }

    @Test
    void requestAllUsersPing() throws AuthorizationException {
        var authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(authentication).getAuthorities();
        doReturn(Optional.of("some_not_null_id")).when(gameInfoService).getGameIdByUsersId("id_1");

        tested.requestAllUsersPing(authentication, List.of("id_1", "id_2"));

        verify(playersNotifier, times(1)).notifyPlayer(user.getId(), new Notification<>(PLAYER_IN_GAME_NOTIFICATION, "id_1"));
        verify(playersNotifier, times(1)).notifyPlayer("id_2", new Notification<>(PING_REQUESTED_NOTIFICATION, user.getId()));
    }

    @Test
    void pingActivityWrongAuthentication() {
        var authentication = mock(Authentication.class);
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();
        var exc = assertThrows(AuthorizationException.class, () -> {
            tested.pingActivity(authentication, "id_1");
        });
        assertEquals("Could not recognize player!", exc.getMessage());
    }

    @Test
    void pingActivityHappyPath() throws AuthorizationException {
        var authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user.getId()))).when(authentication).getAuthorities();

        tested.pingActivity(authentication, "id_1");

        verify(playersNotifier, times(1)).notifyPlayer("id_1", new Notification<>(PLAYER_ACTIVE_NOTIFICATION, user.getId()));
    }
}