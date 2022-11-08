package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.UsersData;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;

@Service
public class UsersService {

    private final ApplicationUserRepository userRepository;
    private final GameInfoService gameInfoService;
    private final PlayersNotifier playersNotifier;

    public UsersService(ApplicationUserRepository userRepository, GameInfoService gameInfoService, PlayersNotifier playersNotifier) {
        this.userRepository = userRepository;
        this.gameInfoService = gameInfoService;
        this.playersNotifier = playersNotifier;
    }

    public UsersData getUsersData(String email) throws AuthorizationException {
        return userRepository.findFirstByEmail(email)
                .map(this::mapToUsersData)
                .orElseThrow(() -> new AuthorizationException("User not found!"));
    }

    private UsersData mapToUsersData(ApplicationUser user) {
        return new UsersData(
                user.getId(),
                user.getNickname(),
                user.getFriends()
        );
    }

    public void requestAllUsersPing(Authentication authentication, List<String> requestedIds) throws AuthorizationException {
        for (String id: requestedIds) {
            requestUsersStatusPing(authentication, id);
        }
    }

    public void requestUsersStatusPing(Authentication authentication, String requestedId) throws AuthorizationException {
        var requestingId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new AuthorizationException("Could not recognize player!"));
        if (gameInfoService.getGameIdByUsersId(requestedId).isPresent()) {
            playersNotifier.notifyPlayer(requestingId, new Notification<>(PLAYER_IN_GAME_NOTIFICATION, requestedId));
            return;
        }
        playersNotifier.notifyPlayer(requestedId, new Notification<>(PING_REQUESTED_NOTIFICATION, requestingId));
    }

    public void pingActivity(Authentication authentication, String requestingPlayersId) throws AuthorizationException {
        var playersId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new AuthorizationException("Could not recognize player!"));
        playersNotifier.notifyPlayer(requestingPlayersId, new Notification<>(PLAYER_ACTIVE_NOTIFICATION, playersId));
    }
}
