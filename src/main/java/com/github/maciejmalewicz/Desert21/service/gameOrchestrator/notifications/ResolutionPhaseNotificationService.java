package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ResolutionPhaseNotificationService {

    public ResolutionPhaseNotificationPair createNotifications(Game game) {
        var currentPlayerNotifications = getCurrentPlayersNotifications(game);
        var opponentNotifications = getOpponentsNotifications(game);
        return new ResolutionPhaseNotificationPair(
                currentPlayerNotifications,
                opponentNotifications
        );
    }

    private List<Notification<?>> getCurrentPlayersNotifications(Game game) {
        var eventResults = game.getCurrentEventResults();
        var forBoth = fetchForBoth(eventResults);
        var forCurrentPlayer = fetchForCurrentPlayer(eventResults);
        var currentPlayerId = game.getStateManager().getCurrentPlayerId();
        var forSpecificPlayer = fetchForSpecificPlayer(eventResults)
                .stream()
                .filter(pair -> pair.getFirst().equals(currentPlayerId))
                .flatMap(pair -> pair.getSecond().stream())
                .toList();
        return Stream.concat(
                Stream.concat(
                        forBoth.stream(), forCurrentPlayer.stream()
                ),
                        forSpecificPlayer.stream()
                )
                .toList();
    }

    private List<Notification<?>> getOpponentsNotifications(Game game) {
        var eventResults = game.getCurrentEventResults();
        var forBoth = fetchForBoth(eventResults);
        var forOpponent = fetchForOpponent(eventResults);
        var currentPlayerId = game.getStateManager().getCurrentPlayerId();
        var opponentId = game.getPlayers().stream()
                .filter(p -> !p.getId().equals(currentPlayerId))
                .findFirst()
                .map(Player::getId)
                .orElse("");
        var forSpecificPlayer = fetchForSpecificPlayer(eventResults)
                .stream()
                .filter(pair -> pair.getFirst().equals(opponentId))
                .flatMap(pair -> pair.getSecond().stream())
                .toList();
        return Stream.concat(
                        Stream.concat(
                                forBoth.stream(), forOpponent.stream()
                        ),
                        forSpecificPlayer.stream()
                )
                .toList();
    }

    private List<Notification<?>> fetchForBoth(List<EventResult> eventResults) {
        return eventResults.stream()
                .flatMap(res -> res.forBoth().stream())
                .toList();
    }

    private List<Notification<?>> fetchForCurrentPlayer(List<EventResult> eventResults) {
        return eventResults.stream()
                .flatMap(res -> res.forCurrentPlayer().stream())
                .toList();
    }

    private List<Notification<?>> fetchForOpponent(List<EventResult> eventResults) {
        return eventResults.stream()
                .flatMap(res -> res.forOpponent().stream())
                .toList();
    }

    private List<Pair<String, List<Notification<?>>>> fetchForSpecificPlayer(List<EventResult> eventResults) {
        return eventResults.stream()
                .map(Notifiable::forSpecificPlayer)
                .toList();
    }
}
