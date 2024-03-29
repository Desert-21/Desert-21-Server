package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersActionDto;
import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersTurnDto;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.TurnResolutionPhaseStartService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerTurnService {

    private final GamePlayerService gamePlayerService;
    private final GameBalanceService gameBalanceService;

    private final PlayersActionsValidatingService playersActionsValidatingService;
    private final GameEventsExecutionService gameEventsExecutionService;

    private final GameRepository gameRepository;

    private final TurnResolutionPhaseStartService turnResolutionPhaseStartService;

    public PlayerTurnService(GamePlayerService gamePlayerService,
                             GameBalanceService gameBalanceService,
                             PlayersActionsValidatingService playersActionsValidatingService,
                             GameEventsExecutionService gameEventsExecutionService,
                             GameRepository gameRepository,
                             TurnResolutionPhaseStartService turnResolutionPhaseStartService) {
        this.gamePlayerService = gamePlayerService;
        this.gameBalanceService = gameBalanceService;
        this.playersActionsValidatingService = playersActionsValidatingService;
        this.gameEventsExecutionService = gameEventsExecutionService;
        this.gameRepository = gameRepository;
        this.turnResolutionPhaseStartService = turnResolutionPhaseStartService;
    }

    @Transactional(rollbackFor = {NotAcceptableException.class, IllegalArgumentException.class, AuthorizationException.class})
    public void executeTurn(Authentication authentication, PlayersTurnDto dto) throws NotAcceptableException, IllegalArgumentException, AuthorizationException {
        var gamePlayer = gamePlayerService.getGamePlayerData(dto.gameId(), authentication);
        if (gamePlayer.game().getStateManager().getGameState() != GameState.AWAITING) {
            throw new NotAcceptableException("Cannot execute turn now!");
        }
        if (!gamePlayer.game().getStateManager().getCurrentPlayerId().equals(gamePlayer.player().getId())) {
            throw new NotAcceptableException("Cannot execute turn now!");
        }
        var actions = dto.actions().stream()
                .map(this::castToClass)
                .toList();
        var context = new TurnExecutionContext(
                gameBalanceService.getGameBalance(),
                gamePlayer.game(),
                gamePlayer.player()
        );
        var mayExecuteActions = playersActionsValidatingService.validatePlayersActions(actions, context);
        if (!mayExecuteActions) {
            throw new NotAcceptableException("Could not execute actions! Stop trying to hack this game, or you will get banned! -,-");
        }
        var eventExecutionResults = gameEventsExecutionService.executeEvents(actions, context);
        var updatedContext = eventExecutionResults.context();
        var eventResults = eventExecutionResults.results();
        var updatedGame = updatedContext.game();

        //setting up for the next phase
        updatedGame.setCurrentEventResults(eventResults);

        turnResolutionPhaseStartService.stateTransition(updatedGame);
    }

    private Action castToClass(PlayersActionDto actionDto) {
        var clazz = actionDto.type().getActionClass();
        return new ObjectMapper().convertValue(actionDto.content(), clazz);
    }
}
