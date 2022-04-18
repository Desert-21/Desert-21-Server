package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersActionDto;
import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersTurnDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.TurnResolutionPhaseStartService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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

    public void executeTurn(Authentication authentication, PlayersTurnDto dto) throws NotAcceptableException {
        var gamePlayer = gamePlayerService.getGamePlayerData(dto.gameId(), authentication);
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
        var updatedContext = gameEventsExecutionService.executeEvents(actions, context);
        var updatedGame = updatedContext.game();

        var savedGame = gameRepository.save(updatedGame);
        turnResolutionPhaseStartService.stateTransition(savedGame);
    }

    private Action castToClass(PlayersActionDto actionDto) {
        var clazz = actionDto.type().getActionClass();
        return new ObjectMapper().convertValue(actionDto.content(), clazz);
    }
}
