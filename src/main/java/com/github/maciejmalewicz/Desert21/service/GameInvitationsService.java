package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.GameInvitation;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameInvitationRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.GameGeneratorService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.GameInvitationNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;

@Service
public class GameInvitationsService {

    private final ApplicationUserRepository applicationUserRepository;
    private final GameRepository gameRepository;
    private final GameInvitationRepository gameInvitationRepository;
    private final PlayersNotifier playersNotifier;
    private final GameGeneratorService gameGeneratorService;

    public GameInvitationsService(ApplicationUserRepository applicationUserRepository, GameRepository gameRepository, GameInvitationRepository gameInvitationRepository, PlayersNotifier playersNotifier, GameGeneratorService gameGeneratorService) {
        this.applicationUserRepository = applicationUserRepository;
        this.gameRepository = gameRepository;
        this.gameInvitationRepository = gameInvitationRepository;
        this.playersNotifier = playersNotifier;
        this.gameGeneratorService = gameGeneratorService;
    }

    /**
     *
     * @return ID of the newly created invitation
     */
    public String inviteToGame(Authentication authentication, String invitedFriendId) throws AuthorizationException, NotAcceptableException {
        var invitingUser = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .flatMap(applicationUserRepository::findById)
                .orElseThrow(() -> new AuthorizationException("Player could not be identified!"));
        invitingUser.getFriends().stream()
                .filter(f -> f.getPlayerId().equals(invitedFriendId))
                .findFirst()
                .orElseThrow(() -> new NotAcceptableException("Invited player could not be found in the friends list!"));
        var isAlreadyInTheGame = Stream.of(invitingUser.getId(), invitedFriendId)
                .map(gameRepository::findByPlayersId)
                .anyMatch(Optional::isPresent);
        if (isAlreadyInTheGame) {
            throw new NotAcceptableException("Player is already in the game and cannot be invited to another one!");
        }
        var gameInvitation = new GameInvitation(invitingUser.getId(), invitedFriendId);
        var savedInvitation = gameInvitationRepository.save(gameInvitation);

        playersNotifier.notifyPlayer(invitedFriendId, new Notification<>(
                GAME_INVITATION_RECEIVED_NOTIFICATION,
                new GameInvitationNotification(savedInvitation.getId(), invitingUser.getNickname()))
        );

        return savedInvitation.getId();
    }

    public void cancelInvitation(Authentication authentication, String invitationId) throws AuthorizationException, NotAcceptableException {
        var invitingUser = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .flatMap(applicationUserRepository::findById)
                .orElseThrow(() -> new AuthorizationException("Player could not be identified!"));
        var invitation = gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotAcceptableException("Game invitation not found!"));
        if (!invitation.getRequestedBy().equals(invitingUser.getId())) {
            throw new NotAcceptableException("Invitation has been requested by another player! Cannot cancel it!");
        }
        gameInvitationRepository.deleteById(invitation.getId());
        playersNotifier.notifyPlayer(invitation.getRequestedFriend(), new Notification<>(
                GAME_INVITATION_CANCELLED_NOTIFICATION,
                new GameInvitationNotification(invitationId, invitingUser.getNickname())
        ));
    }

    public void rejectInvitation(Authentication authentication, String invitationId) throws AuthorizationException, NotAcceptableException {
        var invitedUser = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .flatMap(applicationUserRepository::findById)
                .orElseThrow(() -> new AuthorizationException("Player could not be identified!"));
        var invitation = gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotAcceptableException("Game invitation not found!"));
        if (!invitation.getRequestedFriend().equals(invitedUser.getId())) {
            throw new NotAcceptableException("Game has been requested for another player! Cannot reject invitation!");
        }
        gameInvitationRepository.deleteById(invitation.getId());
        playersNotifier.notifyPlayer(invitation.getRequestedBy(), new Notification<>(
                GAME_INVITATION_REJECTED_NOTIFICATION, new GameInvitationNotification(invitationId, invitedUser.getNickname()))
        );
    }

    public void acceptInvitation(Authentication authentication, String invitationId) throws AuthorizationException, NotAcceptableException {
        var invitedUser = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .flatMap(applicationUserRepository::findById)
                .orElseThrow(() -> new AuthorizationException("Player could not be identified!"));
        var invitation = gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotAcceptableException("Game invitation not found!"));
        if (!invitation.getRequestedFriend().equals(invitedUser.getId())) {
            throw new NotAcceptableException("Game has been requested for another player! Cannot accept invitation!");
        }

        invitation.setAccepted(true);
        gameInvitationRepository.save(invitation);

        playersNotifier.notifyPlayer(invitation.getRequestedBy(), new Notification<>(
                GAME_INVITATION_ACCEPTED_NOTIFICATION, new GameInvitationNotification(invitationId, invitedUser.getNickname()))
        );
    }

    public void confirmReadiness(Authentication authentication, String invitationId) throws AuthorizationException, NotAcceptableException {
        var invitingUser = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .flatMap(applicationUserRepository::findById)
                .orElseThrow(() -> new AuthorizationException("Player could not be identified!"));
        var invitation = gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotAcceptableException("Game invitation not found!"));
        if (!invitation.getRequestedBy().equals(invitingUser.getId())) {
            throw new NotAcceptableException("Game has been requested by another player! Cannot confirm readiness!");
        }
        if (!invitation.isAccepted()) {
            throw new NotAcceptableException("Invitation wasn't accepted yet! You can't confirm readiness before it's accepted.");
        }

        gameGeneratorService.generateGame(invitation.getRequestedFriend(), invitation.getRequestedBy());
        gameInvitationRepository.deleteById(invitation.getId());
    }
}
